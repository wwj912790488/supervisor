package com.arcsoft.supervisor.transcoder.type;

/**
 * @author Bing
 */
public enum EthBondMode {
    /**
     * Round-robin policy: Transmit packets in sequential order from the first
     * available slave through the last. This mode provides load balancing and
     * fault tolerance.
     */
    BALANCE_RR,
    /**
     * Active-backup policy: Only one slave in the bond is active. A different
     * slave becomes active if, and only if, the active slave fails. The bond's
     * MAC address is externally visible on only one port (network adapter) to
     * avoid confusing the switch. This mode provides fault tolerance. The
     * primary option affects the behavior of this mode.
     */
    ACTIVE_BACKUP,
    /**
     * XOR policy: Transmit based on [(source MAC address XOR'd with destination
     * MAC address) modulo slave count]. This selects the same slave for each
     * destination MAC address. This mode provides load balancing and fault
     * tolerance.
     */
    BALANCE_XOR,
    /**
     * Broadcast policy: transmits everything on all slave interfaces. This mode
     * provides fault tolerance.
     */
    BROADCAST,
    /**
     * IEEE 802.3ad Dynamic link aggregation. Creates aggregation groups that
     * share the same speed and duplex settings. Utilizes all slaves in the
     * active aggregator according to the 802.3ad specification. Prerequisites:
     * Ethtool support in the base drivers for retrieving the speed and duplex
     * of each slave.
     * Prerequisites:
     * 1. A switch that supports IEEE 802.3ad Dynamic link
     * aggregation. Most switches will require some type of configuration to
     * enable 802.3ad mode.
     * 2.A switch that supports IEEE 802.3ad Dynamic link
     * aggregation. Most switches will require some type of configuration to
     * enable 802.3ad mode.
     */
    IEEE802_3AD,
    /**
     * Adaptive transmit load balancing: channel bonding that does not require
     * any special switch support. The outgoing traffic is distributed according
     * to the current load (computed relative to the speed) on each slave.
     * Incoming traffic is received by the current slave. If the receiving slave
     * fails, another slave takes over the MAC address of the failed receiving
     * slave. Prerequisites: Ethtool support in the base drivers for retrieving
     * the speed of each slave.
     */
    BALANCE_TLB,
    /**
     * Adaptive load balancing: includes balance-tlb plus receive load balancing
     * (rlb) for IPV4 traffic, and does not require any special switch support.
     * The receive load balancing is achieved by ARP negotiation. The bonding
     * driver intercepts the ARP Replies sent by the local system on their way
     * out and overwrites the source hardware address with the unique hardware
     * address of one of the slaves in the bond such that different peers use
     * different hardware addresses for the server.
     */
    BALANCE_ALB;


    @Override
    public String toString() {
        switch (this) {
            case BALANCE_RR:
                return "balance-rr";
            case ACTIVE_BACKUP:
                return "active-backup";
            case BALANCE_XOR:
                return "balance-xor";
            case BROADCAST:
                return "broadcast";
            case IEEE802_3AD:
                return "802.3ad";
            case BALANCE_TLB:
                return "balance-tlb";
            case BALANCE_ALB:
                return "balance-alb";
            default:
                break;
        }
        return super.toString();
    }

    /**
     * mode
     *
     * @return
     */
    public int getId() {
        return this.ordinal();
    }

    public static EthBondMode getById(int mode) {
        EthBondMode[] modes = EthBondMode.values();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].getId() == mode)
                return modes[i];
        }
        return null;
    }
}
