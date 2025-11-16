package uos.pbl.irumoa.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.pbl.irumoa.program.entity.ActionLog;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
}

