package com.example.Interview_Scheduler.repository;

import com.example.Interview_Scheduler.model.BatchModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BatchRepository extends JpaRepository<BatchModel, Long> {

}