
package com.tenpo.repositoryjpa;


import com.tenpo.model.HistoryAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenpoRepository extends JpaRepository<HistoryAverage, Long> {

}

