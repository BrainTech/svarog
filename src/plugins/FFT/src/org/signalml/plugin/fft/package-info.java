/**
 * This is the library package, which allows to:
 * <ul>
 * <li>compute forward and inverse FFT of real and complex data,</li>
 * <li>calculate the power spectrum of the data (using FFT),</li>
 * <li>apply {@link org.signalml.plugin.fft.export.WindowType windows} to the
 * data before computing FFT/power spectrum.</li>
 * </ul>
 * <p>
 * To actual calculation of the FFT is performed using Piotr Wendykier's JTransforms
 * (class {@code DoubleFFT_1D}).
 * @author Marcin Szumski
 */
package org.signalml.plugin.fft;