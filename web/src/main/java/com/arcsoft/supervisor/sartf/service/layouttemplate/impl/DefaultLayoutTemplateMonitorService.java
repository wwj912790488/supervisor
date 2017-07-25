package com.arcsoft.supervisor.sartf.service.layouttemplate.impl;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateInfo;
import com.arcsoft.supervisor.sartf.repository.layouttemplate.LayoutTemplateRepository;
import com.arcsoft.supervisor.sartf.service.layouttemplate.LayoutTemplateMonitorService;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Sartf
public class DefaultLayoutTemplateMonitorService implements
		LayoutTemplateMonitorService, TransactionSupport, Runnable {
	
	@Autowired
	private LayoutTemplateRepository layoutTemplateRepository;
	
    @Autowired
    private ServletContext context;
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    private WatchService templateWatcher;
    
    private Validator templateValidator = null;
    
    private DocumentBuilder dBuilder;
    
    Path templateDir;
    
    private ExecutorService executor = Executors.newFixedThreadPool(1, NamedThreadFactory.create("LayoutTemplateMonitor"));
    
    protected Logger log = Logger.getLogger(getClass());
    
    private volatile boolean stopped;
    
    public static class Finder extends SimpleFileVisitor<Path> {
    	
    	DefaultLayoutTemplateMonitorService service;
    	HashMap<String, LayoutTemplate> map;
    	
    	public Finder(DefaultLayoutTemplateMonitorService service, HashMap<String, LayoutTemplate> map) {
    		this.service = service;
    		this.map = map;
    	}
    	// Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(final Path file,
                BasicFileAttributes attrs) {
        	service.transactionTemplate.execute(new TransactionCallback<Void>() {
				@Override
				public Void doInTransaction(TransactionStatus status) {					
					service.saveIfNew(file);
					return null;
				}
        	});
        	map.remove(file.getFileName().toString());
            return FileVisitResult.CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            System.err.println(exc);
            return FileVisitResult.CONTINUE;
        }
    }

	@Override
	@PostConstruct
	public void start() {	
		String path = context.getRealPath("/WEB-INF/template/");
		templateDir = Paths.get(path);
		
		try {
			dBuilder = DocumentBuilderFactory.newInstance()
			        .newDocumentBuilder();
		} catch (ParserConfigurationException e3) {
			e3.printStackTrace();
		}
		
		try {		
			String xsdPath = context.getRealPath("/WEB-INF/template/configTemplate.xsd");
			Path xsd = Paths.get(xsdPath);
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema templateSchema = factory.newSchema(xsd.toFile());
			templateValidator = templateSchema.newValidator();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		List<LayoutTemplate> templates = layoutTemplateRepository.findAll();
		
		HashMap<String, LayoutTemplate> map = new HashMap<String, LayoutTemplate>();
		
		for( LayoutTemplate template : templates ) {
			map.put(template.getPath(), template);
		}
		
		Finder finder = new Finder(this, map);
        try {
			Files.walkFileTree(templateDir, finder);
			for(LayoutTemplate template : map.values()) {
				delete(template);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
		try {
			templateWatcher = templateDir.getFileSystem().newWatchService();
			templateDir.register(templateWatcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			stopped = false;
			executor.execute(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public boolean validate(Path path) {
		File file = path.toFile();
		Source xmlFile = new StreamSource(file);
		boolean valid = false;
		try {
			templateValidator.validate(xmlFile);
			valid = true;
		} catch (SAXParseException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valid;
	}
	
	@PreDestroy
	public void stop() {
		stopped = true;
        try {
            templateWatcher.close();
        } catch (IOException e) {
            log.error("Failed to close templateWatcher.", e);
        }
        executor.shutdown();
	}
	
	private LayoutTemplate createFrom(Path path) {
		try {
			LayoutTemplate template = new LayoutTemplate();
			template.setPath(path.getFileName().toString());
			template.setLastUpdate(new Date());
			Document doc = dBuilder.parse(path.toFile());
			Element configElement = (Element) doc.getElementsByTagName("configTemplate").item(0);
			Integer totalWidth = Integer.parseInt(configElement.getElementsByTagName("totalWidth").item(0).getTextContent());
			Integer totalHeight = Integer.parseInt(configElement.getElementsByTagName("totalHeight").item(0).getTextContent());
			LayoutTemplateInfo info = new LayoutTemplateInfo();
			info.setTotalWidth(totalWidth);
			info.setTotalHeight(totalHeight);
			info.setTemplate(template);
			template.setInfo(info);
			NodeList nList = configElement.getElementsByTagName("cell");
			ArrayList<LayoutTemplateCell> cells = new ArrayList<LayoutTemplateCell>();
			for (int i = 0; i < nList.getLength(); i++) {
				Element element = (Element) nList.item(i);
				LayoutTemplateCell cell = new LayoutTemplateCell();
				Integer index = Integer.parseInt(element.getElementsByTagName("index").item(0).getTextContent());
				Integer xPos = Integer.parseInt(element.getElementsByTagName("xPos").item(0).getTextContent());
				Integer yPos = Integer.parseInt(element.getElementsByTagName("yPos").item(0).getTextContent());
				Integer width = Integer.parseInt(element.getElementsByTagName("width").item(0).getTextContent());
				Integer height = Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent());
				cell.setCell_index(index);
				cell.setxPos(xPos);
				cell.setyPos(yPos);
				cell.setWidth(width);
				cell.setHeight(height);
				cell.setTemplate(template);
				cells.add(cell);
			}
			template.setCells(cells);
			
			return template;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void save(Path path) {
		if(validate(path)) {	
			LayoutTemplate template = createFrom(path);
			layoutTemplateRepository.save(template);
		}
	}
	
	private void saveIfNew(Path path) {
		LayoutTemplate template = layoutTemplateRepository.findByPath(path.getFileName().toString());
		if(template == null) {
			save(path);
		}
		
	}
	
	private void delete(Path path) {
		layoutTemplateRepository.deleteByPath(path.toString());
	}
	
	private void delete(LayoutTemplate template) {
		layoutTemplateRepository.delete(template);
	}
	
	private void update(Path path) {
		LayoutTemplate template = layoutTemplateRepository.findByPath(path.getFileName().toString());
		if(template != null) {
			LayoutTemplate newTemplate = createFrom(path);
			template.setLastUpdate(new Date());
			LayoutTemplateInfo info = newTemplate.getInfo();
			info.setTemplate(template);
			template.setInfo(info);
			List<LayoutTemplateCell> cells = template.getCells();
			cells.clear();
			List<LayoutTemplateCell> newcells = newTemplate.getCells();
			for(LayoutTemplateCell cell : newcells) {
				cell.setTemplate(template);
				cells.add(cell);
			}
		} else {
			save(path);
		}
	}

	@Override
	public void run() {
		while(!stopped) {
			try {
				WatchKey watchKey = templateWatcher.take();
				List<WatchEvent<?>> events = watchKey.pollEvents();
				for (final WatchEvent event : events) {
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
					final Path relativePath = ev.context();
					final Path templatePath = templateDir.resolve(relativePath);
					transactionTemplate.execute(new TransactionCallback<Void>() {
						@Override
						public Void doInTransaction(TransactionStatus status) {
							if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
								save(templatePath);					
							}
							if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
								delete(relativePath);
							}
							if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {						
								update(templatePath);				
							}
							return null;
						}
						
					});
					
				}
				watchKey.reset();			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
