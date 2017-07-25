package com.arcsoft.supervisor.repository.packages;

import java.util.List;

import com.arcsoft.supervisor.model.domain.setuppackage.SetupPackage;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The repository interface for <tt>SetupPackage</tt>
 *
 * @author jt.
 */
public interface PackageRepository extends JpaRepository<SetupPackage, Integer> {

	List<SetupPackage> findByVersion(String packageVersion);
	
	List<SetupPackage> findByIsDeployVersion(boolean isDeployVersion);

	Long countByVersion(String version);
}
