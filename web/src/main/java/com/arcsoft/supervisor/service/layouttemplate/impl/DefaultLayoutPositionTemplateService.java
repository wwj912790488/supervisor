package com.arcsoft.supervisor.service.layouttemplate.impl;

import com.arcsoft.supervisor.model.domain.layouttemplate.*;
import com.arcsoft.supervisor.repository.layouttemplate.LayoutPositionTemplateRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.layouttemplate.LayoutPositionTemplateService;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DefaultLayoutPositionTemplateService implements LayoutPositionTemplateService, TransactionSupport, Runnable {

    @Autowired
    private LayoutPositionTemplateRepository layoutPositionTemplateRepository;

    @Autowired
    private ServletContext context;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private WatchService templateWatcher;

    private Validator templateValidator = null;

    private DocumentBuilder dBuilder;

    Path templateDir;

    private ExecutorService executor = Executors.newFixedThreadPool(1, NamedThreadFactory.create("LayoutPositionTemplateMonitor"));

    protected Logger log = Logger.getLogger(getClass());

    private volatile boolean stopped;

    public static class Finder extends SimpleFileVisitor<Path> {

        DefaultLayoutPositionTemplateService service;

        public Finder(DefaultLayoutPositionTemplateService service) {
            this.service = service;
        }
        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(final Path file,
                                         BasicFileAttributes attrs) {
            service.transactionTemplate.execute(new TransactionCallback<Void>() {
                @Override
                public Void doInTransaction(TransactionStatus status) {
                    service.save(file);
                    return null;
                }
            });
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
            return FileVisitResult.CONTINUE;
        }
    }

    @PostConstruct
    public void start() {
        String path = context.getRealPath("/WEB-INF/layoutpositiontemplate/files");
        templateDir = Paths.get(path);

        try {
            dBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        }

        try {
            String xsdPath = context.getRealPath("/WEB-INF/layoutpositiontemplate/layoutPositionTemplate.xsd");
            Path xsd = Paths.get(xsdPath);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema templateSchema = factory.newSchema(xsd.toFile());
            templateValidator = templateSchema.newValidator();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        Finder finder = new Finder(this);
        try {
            Files.walkFileTree(templateDir, finder);
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

    private LayoutPositionTemplate createFrom(Path path) {
        try {
            LayoutPositionTemplate template = new LayoutPositionTemplate();
            Document doc = dBuilder.parse(path.toFile());
            Element configElement = (Element) doc.getElementsByTagName("LayoutPositionTemplate").item(0);
            String guid = configElement.getElementsByTagName("guid").item(0).getTextContent();
            Integer rowCount = Integer.parseInt(configElement.getElementsByTagName("rowCount").item(0).getTextContent());
            Integer columnCount = Integer.parseInt(configElement.getElementsByTagName("columnCount").item(0).getTextContent());
            template.setGuid(guid);
            template.setRowCount(rowCount);
            template.setColumnCount(columnCount);
            NodeList nList = configElement.getElementsByTagName("position");
            ArrayList<LayoutPosition> positions = new ArrayList<LayoutPosition>();
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                LayoutPosition cell = new LayoutPosition();
                Integer row = Integer.parseInt(element.getElementsByTagName("row").item(0).getTextContent());
                Integer column = Integer.parseInt(element.getElementsByTagName("column").item(0).getTextContent());
                Integer x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
                Integer y = Integer.parseInt(element.getElementsByTagName("y").item(0).getTextContent());
                cell.setRow(row);
                cell.setColumn(column);
                cell.setX(x);
                cell.setY(y);
                cell.setTemplate(template);
                positions.add(cell);
            }
            template.setPositions(positions);

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
            LayoutPositionTemplate template = createFrom(path);
            LayoutPositionTemplate oldTemplate = layoutPositionTemplateRepository.findFirstByGuid(template.getGuid());
            if(oldTemplate == null) {
                layoutPositionTemplateRepository.save(template);
            }
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
                            }
                            if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                save(templatePath);
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

    @Override
    public List<LayoutPositionTemplate> findAll() {
        return layoutPositionTemplateRepository.findAll();
    }


}
