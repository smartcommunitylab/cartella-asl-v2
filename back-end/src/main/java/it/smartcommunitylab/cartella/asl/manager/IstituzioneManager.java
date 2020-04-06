package it.smartcommunitylab.cartella.asl.manager;

import java.util.List;

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
import it.smartcommunitylab.cartella.asl.repository.IstituzioneRepository;
import it.smartcommunitylab.cartella.asl.util.Constants;

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
		Istituzione istituzione = istituzioneRepository.getOne(istitutoId);
		return istituzione;
	}
	
	public void updateIstituzioneHoursThreshold(String id, Double hours) {
		istituzioneRepository.updateHoursThreshold(id, hours);
	}	


}
