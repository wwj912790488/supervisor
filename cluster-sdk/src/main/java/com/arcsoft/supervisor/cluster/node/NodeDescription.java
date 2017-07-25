package com.arcsoft.supervisor.cluster.node;



/**
 * A class description node info.
 * 
 * @author fjli
 * @author zw
 */
public class NodeDescription {

	private int type;
	private String id;
	private String name;
	private String ip;
	private int port;
    private String netmask;
    private String eth;
    private String gateway;
    private String functions;
	private Integer gpus;


	/**
	 * Construct new node description.
	 * 
	 * @param type - the node type
	 * @param id - the node id
	 * @param name - the node name
	 * @param ip - the node ip
	 * @param port - the node port
	 */
	public NodeDescription(int type, String id, String name, String ip, int port) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
	}


	public NodeDescription(int type, String id, String name, String ip, int port,int gpus) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.gpus=gpus;
	}

    public NodeDescription(int type, String id, String name, String ip, int port, String netmask, String eth, String gateway, String functions) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.netmask = netmask;
        this.eth = eth;
        this.gateway = gateway;
        this.functions = functions;
    }


	/**
	 * Returns the node type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the node unique id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the node name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the node IP.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Return the port.
	 */
	public int getPort() {
		return port;
	}

    public String getNetmask() {
        return netmask;
    }

    public String getEth() {
        return eth;
    }

    public String getGateway() {
        return gateway;
    }

    public String getFunctions() {
        return functions;
    }

	public Integer getGpus() {
		return gpus;
	}



	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NodeDescription{");
        sb.append("type=").append(type);
        sb.append(", id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", port=").append(port);
        sb.append(", netmask='").append(netmask).append('\'');
        sb.append(", eth='").append(eth).append('\'');
        sb.append(", gateway='").append(gateway).append('\'');
        sb.append(", functions='").append(functions).append('\'');
		sb.append(", gpus='").append(gpus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
