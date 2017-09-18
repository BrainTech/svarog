/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml;

import org.junit.ClassRule;
import org.junit.rules.Timeout;
/**
 *
 * @author bednarek
 */
public class BaseTestCase {
    @ClassRule
    public static Timeout globalTimeout = Timeout.seconds(60);
    
    
}
