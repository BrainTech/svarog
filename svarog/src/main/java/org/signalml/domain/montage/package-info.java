/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * An EEG voltage signal represents a difference between the voltages at two
 * electrodes, the display of the EEG for the reading encephalographer may be
 * set up in one of several ways. The representation of the EEG channels is
 * referred to as a montage.
 * <ul>
 * <li>Bipolar montage</li>
 * Each channel (i.e., waveform) represents the difference between two adjacent
 * electrodes. The entire montage consists of a series of these channels.
 * For example, the channel "Fp1-F3" represents the difference in voltage between
 * the Fp1 electrode and the F3 electrode. The next channel in the montage,
 * "F3-C3," represents the voltage difference between F3 and C3, and so on
 * through the entire array of electrodes.
 * <li>Referential montage</li>
 * Each channel represents the difference between a certain electrode and
 * a designated reference electrode. There is no standard position for this
 * reference; it is, however, at a different position than the "recording"
 * electrodes. Midline positions are often used because they do not amplify
 * the signal in one hemisphere vs. the other. Another popular reference is
 * "linked ears," which is a physical or mathematical average of electrodes
 * attached to both earlobes or mastoids.
 * <li>Average reference montage</li>
 * The outputs of all of the amplifiers are summed and averaged, and this averaged
 * signal is used as the common reference for each channel.
 *
 * This package contains Montages and structures associated
 * with them.
 * The most important are MontageGenerators, which can create
 * a montage of a specified type (depending on the type of the generator).
 * Single montage contains Channels which are composed
 * of SourceChannels (as primary and reference channels).
 * There are also definitions of filters of montage samples. <br>
 * source: {@code http://en.wikipedia.org/wiki/Electroencephalography}
 */
package org.signalml.domain.montage;
