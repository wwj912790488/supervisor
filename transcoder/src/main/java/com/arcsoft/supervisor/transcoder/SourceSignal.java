package com.arcsoft.supervisor.transcoder;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

public class SourceSignal {
    /**
     * master+backup+pad
     */
    public static final int MODE_MASTER_BACKUP_PAD = 0;
    /**
     * master+pad
     */
    public static final int MODE_MASTER_PAD = 1;
    /**
     * backup+pad
     */
    public static final int MODE_BACKUP_PAD = 2;
    /**
     * only pad
     */
    public static final int MODE_PAD = 3;


    public static final int STATUS_ABNORMAL = 0;
    public static final int STATUS_NORMAL = 1;

    public static final int SIGNAL_MASTER = 0;
    public static final int SIGNAL_BACKUP = 1;
    public static final int SIGNAL_PAD = 2;

    private byte masterSignalStatus;
    private byte backupSignalStatus;
    private byte padSignalStatus;
    private byte currSignal;
    private byte signalMode;
    private Logger log = Logger.getLogger(getClass());

    public SourceSignal() {

    }

    public SourceSignal(int masterSignalStatus, int backupSignalStatus, int padSignalStatus, int currSignal, int signalMode) {
        this.masterSignalStatus = (byte) masterSignalStatus;
        this.backupSignalStatus = (byte) backupSignalStatus;
        this.padSignalStatus = (byte) padSignalStatus;
        this.currSignal = (byte) currSignal;
        this.signalMode = (byte) signalMode;
    }

    public int getMasterSignalStatus() {
        return masterSignalStatus & 0x00FF;
    }

    public int getBackupSignalStatus() {
        return backupSignalStatus & 0x00FF;
    }

    public int getPadSignalStatus() {
        return padSignalStatus & 0x00FF;
    }

    /**
     * @return {@link #SIGNAL_MASTER}, {@link #SIGNAL_BACKUP} or {@link #SIGNAL_PAD}
     */
    public int getCurrSignal() {
        return currSignal & 0x00FF;
    }

    public int getSignalMode() {
        return signalMode & 0x00FF;
    }

    /**
     * Returns a xml document to represent this object.
     */
    public Document toXmlDOM() {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("signal");
            addChildNode(doc, root, "mode", getSignalMode());
            addChildNode(doc, root, "current", getCurrSignal());
            addChildNode(doc, root, "master", getMasterSignalStatus());
            addChildNode(doc, root, "backup", getBackupSignalStatus());
            addChildNode(doc, root, "pad", getPadSignalStatus());
            doc.appendChild(root);
        } catch (Exception e) {
            log.error("export source signal to dom failed.", e);
        }
        return doc;
    }

    private static void addChildNode(Document doc, Element root, String name, int value) {
        Element e = doc.createElement(name);
        e.appendChild(doc.createTextNode(String.valueOf(value)));
        root.appendChild(e);
    }

}
