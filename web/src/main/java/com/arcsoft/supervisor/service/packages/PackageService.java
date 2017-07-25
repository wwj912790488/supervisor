package com.arcsoft.supervisor.service.packages;

import java.util.List;

import com.arcsoft.supervisor.model.domain.setuppackage.SetupPackage;

/**
 * Interface for handling logic of <tt>user</tt>.
 *
 * @author jt.
 */
public interface PackageService {

	void savePackage(SetupPackage setupPackage);
	
	void savePackage(String version,String UploadDate, String UploadPath);

    void deletePackage(SetupPackage setupPackage);

    void deleteById(Integer id);
   
    List<SetupPackage> listAll();

    List<SetupPackage> getPackageByVersion(String packageVersion);

	void deployPackage(SetupPackage setupPackage,String uploadPath);

	SetupPackage getPackageById(Integer id);

}


