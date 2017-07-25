package com.arcsoft.supervisor.model.vo.task.profile;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * <code>TSOverUDP</code> output.
 *
 * @author zw.
 */
@JsonTypeName("UdpStreaming-UDPOverTS")
public class TsOverUdpOutput extends TaskOutput {

    @JsonProperty("outputbuffersize")
    private Integer bufferSize;

    @JsonProperty("outputTTL")
    private Integer ttl;

    @JsonProperty("outputport")
    private Integer port;

    @JsonProperty("outputtsoption")
    private TSOption tsOption;


    public Integer getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public TSOption getTsOption() {
        return tsOption;
    }

    public void setTsOption(TSOption tsOption) {
        this.tsOption = tsOption;
    }

    public class TSOption {
        private String tsservicename;

        private String tspmtpid;

        private String tsserviceprovider;

        private String tsvideopid;

        private String tsserviceid;

        private String tsaudiopid;

        private String tstotalbitrate;

        private String tspcrpid;

        private String tsnetworkid;

        private String tstransportid;

        private String tsinserttottdt;

        private String tstottdtperiod;

        private String tspcrperiod;

        private String tspatperiod;

        private String tssdtperiod;

        private String tsprivatemetadatapid;

        private String tsprivatemetadatatype;


        public String getTsservicename() {
            return tsservicename;
        }

        public void setTsservicename(String tsservicename) {
            this.tsservicename = tsservicename;
        }

        public String getTspmtpid() {
            return tspmtpid;
        }

        public void setTspmtpid(String tspmtpid) {
            this.tspmtpid = tspmtpid;
        }

        public String getTsserviceprovider() {
            return tsserviceprovider;
        }

        public void setTsserviceprovider(String tsserviceprovider) {
            this.tsserviceprovider = tsserviceprovider;
        }

        public String getTsvideopid() {
            return tsvideopid;
        }

        public void setTsvideopid(String tsvideopid) {
            this.tsvideopid = tsvideopid;
        }

        public String getTsserviceid() {
            return tsserviceid;
        }

        public void setTsserviceid(String tsserviceid) {
            this.tsserviceid = tsserviceid;
        }

        public String getTsaudiopid() {
            return tsaudiopid;
        }

        public void setTsaudiopid(String tsaudiopid) {
            this.tsaudiopid = tsaudiopid;
        }

        public String getTstotalbitrate() {
            return tstotalbitrate;
        }

        public void setTstotalbitrate(String tstotalbitrate) {
            this.tstotalbitrate = tstotalbitrate;
        }

        public String getTspcrpid() {
            return tspcrpid;
        }

        public void setTspcrpid(String tspcrpid) {
            this.tspcrpid = tspcrpid;
        }

        public String getTsnetworkid() {
            return tsnetworkid;
        }

        public void setTsnetworkid(String tsnetworkid) {
            this.tsnetworkid = tsnetworkid;
        }

        public String getTstransportid() {
            return tstransportid;
        }

        public void setTstransportid(String tstransportid) {
            this.tstransportid = tstransportid;
        }

        public String getTsinserttottdt() {
            return tsinserttottdt;
        }

        public void setTsinserttottdt(String tsinserttottdt) {
            this.tsinserttottdt = tsinserttottdt;
        }

        public String getTstottdtperiod() {
            return tstottdtperiod;
        }

        public void setTstottdtperiod(String tstottdtperiod) {
            this.tstottdtperiod = tstottdtperiod;
        }

        public String getTspcrperiod() {
            return tspcrperiod;
        }

        public void setTspcrperiod(String tspcrperiod) {
            this.tspcrperiod = tspcrperiod;
        }

        public String getTspatperiod() {
            return tspatperiod;
        }

        public void setTspatperiod(String tspatperiod) {
            this.tspatperiod = tspatperiod;
        }

        public String getTssdtperiod() {
            return tssdtperiod;
        }

        public void setTssdtperiod(String tssdtperiod) {
            this.tssdtperiod = tssdtperiod;
        }

        public String getTsprivatemetadatapid() {
            return tsprivatemetadatapid;
        }

        public void setTsprivatemetadatapid(String tsprivatemetadatapid) {
            this.tsprivatemetadatapid = tsprivatemetadatapid;
        }

        public String getTsprivatemetadatatype() {
            return tsprivatemetadatatype;
        }

        public void setTsprivatemetadatatype(String tsprivatemetadatatype) {
            this.tsprivatemetadatatype = tsprivatemetadatatype;
        }
    }
}
