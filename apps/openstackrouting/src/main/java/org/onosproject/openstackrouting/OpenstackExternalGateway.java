/*
 * Copyright 2016 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.openstackrouting;

import org.onlab.packet.Ip4Address;

import java.util.HashMap;

/**
 * A configurable external gateway modes extension model in openstack router.
 */
public final class OpenstackExternalGateway {

    private String networkId;
    private boolean enablePNAT;
    private HashMap<String, Ip4Address> externalFixedIps;

    private OpenstackExternalGateway(String networkId, boolean enablePNAT,
                                     HashMap externalFixedIps) {
        this.networkId = networkId;
        this.enablePNAT = enablePNAT;
        this.externalFixedIps = externalFixedIps;
    }

    /**
     * Returns network ID.
     *
     * @return Network ID
     */
    public String networkId() {
        return networkId;
    }

    /**
     * Returns the PNAT status for external gateway.
     *
     * @return PNAT status
     */
    public boolean isEnablePNAT() {
        return enablePNAT;
    }

    /**
     * An Openstack External Gateway Builder class.
     */
    public static final class Builder {
        private String networkId;
        private boolean enablePNAT;
        private HashMap<String, Ip4Address> externalFixedIPs;

        Builder() {
            externalFixedIPs = new HashMap<>();
        }

        /**
         * Sets network ID.
         *
         * @param networkId Network ID
         * @return Builder object
         */
        public Builder networkId(String networkId) {
            this.networkId = networkId;
            return this;
        }

        /**
         * Sets whether PNAT status is enabled or not.
         *
         * @param enablePNAT true if PNAT status is enabled, false otherwise
         * @return Builder object
         */
        public Builder enablePNAT(boolean enablePNAT) {
            this.enablePNAT = enablePNAT;
            return this;
        }

        /**
         * Sets external fixed IP address information.
         *
         * @param externalFixedIPs External fixed IP information
         * @return Builder object
         */
        public Builder externalFixedIPs(HashMap<String, Ip4Address> externalFixedIPs) {
            this.externalFixedIPs.putAll(externalFixedIPs);
            return this;
        }

        /**
         * Builds an OpenstackExternalGateway object.
         *
         * @return OpenstackExternalGateway object
         */
        public OpenstackExternalGateway build() {
            return new OpenstackExternalGateway(networkId, enablePNAT, externalFixedIPs);
        }
    }

}
