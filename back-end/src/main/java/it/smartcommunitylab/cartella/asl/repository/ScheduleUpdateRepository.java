package it.smartcommunitylab.cartella.asl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.cartella.asl.model.ScheduleUpdate;

@Repository
public interface ScheduleUpdateRepository extends JpaRepository<ScheduleUpdate, String> {
}