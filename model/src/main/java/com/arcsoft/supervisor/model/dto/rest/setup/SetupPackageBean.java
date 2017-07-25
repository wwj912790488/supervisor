package com.arcsoft.supervisor.model.dto.rest.setup;

/**
 * The class used for serialize or de-serialize object for setup
 *
 * @author jt.
 */
public class SetupPackageBean {


    private String url;
    private String hash;

   

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static SetupPackageBean build(String url, String hash){
    	SetupPackageBean setupPackageBean = new SetupPackageBean();
    	setupPackageBean.setUrl(url);
    	setupPackageBean.setHash(hash);
        return setupPackageBean;
    }
}
