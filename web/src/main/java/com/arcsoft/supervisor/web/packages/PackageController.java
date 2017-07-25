package com.arcsoft.supervisor.web.packages;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.server.NameExistsException;
import com.arcsoft.supervisor.exception.user.UserExistsException;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.setuppackage.SetupPackage;
import com.arcsoft.supervisor.service.packages.PackageService;
import com.arcsoft.supervisor.service.server.OpsServerService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Base controller class for <tt>User</tt> module.
 *
 * @author jt.
 */
@Controller
@RequestMapping("/package")
public class PackageController extends ControllerSupport{

    private static final String VIEW_OPS = "/package/ops";
    private static final String VIEW_PACKAGE = "/package/package";


    @Autowired
    private PackageService packageService;
    
    @Autowired
    private OpsServerService<OpsServer> opsServerService;
    
    @RequestMapping(value = "/package", method = RequestMethod.GET)
    public String showPackage(){
        return VIEW_PACKAGE;
    }

    @RequestMapping(value = "/packages", method = RequestMethod.GET)
    @ResponseBody
    public List<SetupPackage> getPackages() {
        return packageService.listAll();
    }

    @RequestMapping(value = "/addPackage", method = RequestMethod.POST)
    @ResponseBody
    public String addPackage(SetupPackage setupPackage) {
        try {
            packageService.savePackage(setupPackage);
            return setupPackage.getVersion();
        }
        catch(NameExistsException e) {
            return "";
        }
    }

    @RequestMapping(value = "/deletePackage", method = RequestMethod.POST)
    @ResponseBody
    public void deletePackage(String packageInfo,HttpServletRequest request) {
        try {
        	List<SetupPackage> list = JsonMapper.getMapper().readValue(
        			packageInfo,
                    JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, SetupPackage.class));
        	
        	String uploadFilePath = request.getServletContext().getRealPath("/WEB-INF/");
        	String deleteFilePath = packageService.getPackageById(list.get(0).getId()).getUploadPath();
        	
        	packageService.deletePackage(list.get(0));
        	Files.delete(Paths.get(uploadFilePath,deleteFilePath));
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @RequestMapping(value = "/deployPackage", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult deployPackage(String packageInfo, HttpServletRequest request) {
    	
    	JsonResult result = JsonResult.fromSuccess();
		String uploadFilePath = request.getServletContext().getRealPath(
				"/WEB-INF/");
    	try {
        	List<SetupPackage> list = JsonMapper.getMapper().readValue(
        			packageInfo,
                    JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, SetupPackage.class));
                    
        	packageService.deployPackage(list.get(0),uploadFilePath);
        	result.setCode(0);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(1);
        }
    	
    	return result;
    }
    
	@RequestMapping(value = "/uploadPackage", headers = "content-type=multipart/form-data", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public JsonResult uploadPackage( @RequestParam("setup_package") MultipartFile multipartFile, HttpServletRequest request)
    {
		JsonResult result = JsonResult.fromSuccess();
		
		if (multipartFile != null && multipartFile.getSize()>0) {
			String uploadFilePath = request.getServletContext().getRealPath(
					"/WEB-INF/setup/");
			Date date = new Date();
			
			try {
				Files.write(Paths.get(uploadFilePath, date.getTime() + "-" + multipartFile.getOriginalFilename()), multipartFile.getBytes());
				result.put("url",
						"setup/" + date.getTime() + "-" + multipartFile.getOriginalFilename());
				String md5 = DigestUtils.md5Hex(multipartFile.getBytes());
				result.put("md5",md5);
			} catch (IOException e) {
				result.setCode(1);
				e.printStackTrace();
			}

		}else{
			result.setCode(2);
		}
		return result;
    }
	
	//OPS
    @RequestMapping(value = "/ops", method = RequestMethod.GET)
    public String showops(){
        return VIEW_OPS;
    }
    
    @RequestMapping(value = "/opss", method = RequestMethod.GET)
    @ResponseBody
    public List<OpsServer> getOPSs() {
        return opsServerService.findAll();
    }
    
    @RequestMapping(value = "/deleteOps", method = RequestMethod.POST)
    @ResponseBody
    public void deleteOps(String opsInfo) {
        try {
        	List<OpsServer> list = JsonMapper.getMapper().readValue(
        			opsInfo,
                    JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, OpsServer.class));

        	opsServerService.delete(list.get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
