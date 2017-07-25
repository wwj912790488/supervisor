package com.arcsoft.supervisor.service.packages.impl;

import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.setuppackage.SetupPackage;
import com.arcsoft.supervisor.repository.packages.PackageRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.packages.PackageService;
import com.arcsoft.supervisor.service.server.OpsServerOperator;
import com.arcsoft.supervisor.service.server.OpsServerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author jt.
 */
@Service
public class DefaultPackageServiceImpl extends ServiceSupport implements PackageService {


    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private OpsServerOperator<OpsServer> opsServerOperator;
    
    @Autowired
    private OpsServerService<OpsServer> opsServerService;

    @Override
    public void savePackage(SetupPackage setupPackage) {
    	if(isPackageExists(setupPackage.getVersion()))
    	{
    		throw new NameExistsException(setupPackage.getVersion());
    	}
    	 else {
    		 packageRepository.save(setupPackage);
    	}
    }
    
    
    private boolean isPackageExists(String version) {
    	Long c = packageRepository.countByVersion(version);
    	return c != null && c.longValue() > 0;
    }
    
    
    @Override
    public void savePackage(String version,String UploadDate, String UploadPath)
    {
    	// ToDo:
    }
    
    
    @Override
    public void deletePackage(SetupPackage setupPackage) {
    	 try{
    		 packageRepository.delete(setupPackage.getId());
         }catch (EmptyResultDataAccessException e){

         }
    }

    
    @Override
    public void deleteById(Integer id) {
        try{
            packageRepository.delete(id);
        }catch (EmptyResultDataAccessException e){

        }
    }

    
    @Override
    public List<SetupPackage> listAll() {
        return packageRepository.findAll();
    } 
    
    
    @Override
    public List<SetupPackage> getPackageByVersion(String packageVersion)
    {
    	return packageRepository.findByVersion(packageVersion);
    }
    
    
    @Override
    public void deployPackage(SetupPackage setupPackage, String uploadPath)
    {
    	SetupPackage packet = packageRepository.findOne(setupPackage.getId());
    	String path = packet.getUploadPath();
    	String hash = packet.getFileHash();
    	
    	List<SetupPackage> packages =  packageRepository.findAll();
    	List<SetupPackage> curPackage = packageRepository.findByIsDeployVersion(true);
    	if(!curPackage.isEmpty())
		{
    		for(int nPackage = 0;nPackage<curPackage.size();nPackage++)
    		{
    			for(int nIndex = 0;nIndex< packages.size();nIndex++)
    			{
    				if (packages.get(nIndex).getId() == curPackage.get(nPackage).getId())
    				{
    					packages.get(nIndex).setIsDeployVersion(false);
    					break;
    				}
    			}
    		}
		}
    	
    	for(int nIndex = 0;nIndex< packages.size();nIndex++)
    	{
    		if (packages.get(nIndex).getId() == setupPackage.getId())
    		{
    			packages.get(nIndex).setIsDeployVersion(true);
    			break;
    		}
    	}
    	
    	packageRepository.flush();
    	
    	try {
			Files.copy(Paths.get(uploadPath, path), Paths.get(uploadPath, "setup/Setup.exe"), StandardCopyOption.REPLACE_EXISTING );
		} catch (IOException e) {

			e.printStackTrace();
		}
    	
    	List<OpsServer> list = opsServerService.findAll();

    	// Todo copy to deploy path and opsService doPost all Ops
    	for (int i = 0; i < list.size(); i++) {
			opsServerOperator.deployPackage(list.get(i), path, hash);
		}
    }
    
    @Override
    public SetupPackage getPackageById(Integer id)
    {
    	return packageRepository.findOne(id);
    }
}
