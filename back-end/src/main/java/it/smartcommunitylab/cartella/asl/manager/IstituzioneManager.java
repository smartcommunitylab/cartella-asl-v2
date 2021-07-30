package it.smartcommunitylab.cartella.asl.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import it.smartcommunitylab.cartella.asl.model.Istituzione;
import it.smartcommunitylab.cartella.asl.model.report.ReportIstitutoEnte;
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;
import it.smartcommunitylab.cartella.asl.util.Utils;

@Repository
@Transactional
public class IstituzioneManager extends DataEntityManager {
	@Autowired
	private IstituzioneRepository istituzioneRepository;

	public Page<Istituzione> findIstituti(String text, double[] coordinate, Integer distance, Pageable pageRequest) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Istituzione> cq1 = cb.createQuery(Istituzione.class);
		Root<Istituzione> istituto = cq1.from(Istituzione.class);

		CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
		cq2.select(cb.count(cq2.from(Istituzione.class)));

		List<Predicate> predicates = Lists.newArrayList();

		if (coordinate != null && distance != null) {
			double t1 = distance / Math.abs(Math.cos(Math.toRadians(coordinate[0])) * Constants.KM_IN_ONE_LAT);
			double t2 = distance / Constants.KM_IN_ONE_LAT;

			double lonA = coordinate[1] - t1;
			double lonB = coordinate[1] + t1;

			double latA = coordinate[0] - t2;
			double latB = coordinate[0] + t2;

			Predicate predicate1 = cb.between(istituto.get("latitude").as(Double.class), latA, latB);
			Predicate predicate2 = cb.between(istituto.get("longitude").as(Double.class), lonA, lonB);
			Predicate geoPredicate = cb.and(predicate1, predicate2);

			predicates.add(geoPredicate);
		}

		if (text != null && !text.isEmpty()) {
			String filter = "%" + text.trim() + "%";
			Predicate textPredicate = cb.like(istituto.get("name").as(String.class), filter);

			predicates.add(textPredicate);
		}

		cq1.where(predicates.toArray(new Predicate[predicates.size()]));
		cq2.where(predicates.toArray(new Predicate[predicates.size()]));

		TypedQuery<Istituzione> query1 = em.createQuery(cq1);
		TypedQuery<Long> query2 = em.createQuery(cq2);

		query1.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query1.setMaxResults(pageRequest.getPageSize());

		List<Istituzione> result = query1.getResultList();
		long total = query2.getSingleResult();

		Page<Istituzione> page = new PageImpl<Istituzione>(result, pageRequest, total);

		return page;
	}
	
	public Istituzione getIstituto(String istitutoId) {
		Istituzione istituzione = istituzioneRepository.findById(istitutoId).orElse(null);
		return istituzione;
	}
	
	public void updateIstituzioneHoursThreshold(String id, Double hours) {
		istituzioneRepository.updateHoursThreshold(id, hours);
	}

	public Page<Istituzione> findIstituti(String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT DISTINCT i FROM Istituzione i");
		if(Utils.isNotEmpty(text)) {
			sb.append(" WHERE UPPER(i.name) LIKE (:text) OR UPPER(i.cf) LIKE (:text)");
		}
		sb.append(" ORDER BY i.name ASC");
		String q = sb.toString();
		
		TypedQuery<Istituzione> query = em.createQuery(q, Istituzione.class);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		List<Istituzione> list = query.getResultList();
		
		Query cQuery = queryToCount(q.replaceAll("DISTINCT i","COUNT(DISTINCT i)"), query);
		long total = (Long) cQuery.getSingleResult();

		Page<Istituzione> page = new PageImpl<Istituzione>(list, pageRequest, total);
		return page;
	}	

	public Page<ReportIstitutoEnte> findIstitutiByEnte(String enteId, String text, Pageable pageRequest) {
		StringBuilder sb = new StringBuilder("SELECT i.id, COUNT(aa.id) FROM Istituzione i LEFT JOIN"
				+ " AttivitaAlternanza aa ON i.id=aa.istitutoId AND aa.enteId=(:enteId) AND aa.dataInizio<=(:date) AND aa.dataFine>=(:date)");
		if(Utils.isNotEmpty(text)) {
			sb.append(" WHERE UPPER(i.name) LIKE (:text) OR UPPER(i.cf) LIKE (:text)");
		}
		sb.append(" GROUP BY i.id ORDER BY COUNT(aa.id) DESC");
		String q = sb.toString();
		
		Query query = em.createQuery(q);
		query.setParameter("enteId", enteId);
		LocalDate localDate = LocalDate.now();
		query.setParameter("date", localDate);
		if(Utils.isNotEmpty(text)) {
			query.setParameter("text", "%" + text.trim().toUpperCase() + "%");
		}
		query.setFirstResult((pageRequest.getPageNumber()) * pageRequest.getPageSize());
		query.setMaxResults(pageRequest.getPageSize());
		@SuppressWarnings("unchecked")
		List<Object[]> rows = query.getResultList();
		List<ReportIstitutoEnte> list = new ArrayList<>();
		for (Object[] obj : rows) {
			String istitutoId = (String) obj[0];
			Istituzione istituto = getIstituto(istitutoId);
			Long attivita = (Long) obj[1];
			ReportIstitutoEnte report = new ReportIstitutoEnte();
			report.setIstituto(istituto);
			report.setAttivitaInCorso(attivita);
			list.add(report);
		}

		String counterQuery = q.replace("SELECT i.id, COUNT(aa.id)", "SELECT COUNT(DISTINCT i)")
				.replace("GROUP BY i.id ORDER BY COUNT(aa.id) DESC", "");
		Query cQuery = queryToCount(counterQuery, query);
		long total = (Long) cQuery.getSingleResult();
		Page<ReportIstitutoEnte> page = new PageImpl<ReportIstitutoEnte>(list, pageRequest, total);
		return page;
	}

	public Istituzione updateIstituto(Istituzione istituto) {
		Istituzione dbIst = istituzioneRepository.findById(istituto.getId()).orElse(null);
		if(dbIst != null) {
			dbIst.setName(istituto.getName());
			dbIst.setCf(istituto.getCf());
			dbIst.setAddress(istituto.getAddress());
			dbIst.setPhone(istituto.getPhone());
			dbIst.setEmail(istituto.getEmail());
			dbIst.setPec(istituto.getPec());
			dbIst.setLatitude(istituto.getLatitude());
			dbIst.setLongitude(istituto.getLongitude());
			dbIst.setRdpAddress(istituto.getRdpAddress());
			dbIst.setRdpEmail(istituto.getRdpEmail());
			dbIst.setRdpName(istituto.getRdpName());
			dbIst.setRdpPhoneFax(istituto.getRdpPhoneFax());
			dbIst.setPrivacyLink(istituto.getPrivacyLink());
			istituzioneRepository.save(dbIst);
		}
		return null;
	}
}
