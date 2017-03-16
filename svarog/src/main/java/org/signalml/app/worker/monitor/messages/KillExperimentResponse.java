/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author marian
 */
public class KillExperimentResponse extends Message {
	@JsonProperty("experiment_id")
	String experimentId;
	
}
