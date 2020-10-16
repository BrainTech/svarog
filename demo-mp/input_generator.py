#!/usr/bin/env python
import numpy
import pylab
import sys

sampling_frequency = 512. # Hz

##################################################
# PLEASE REFER TO README.txt FOR MORE INFORMATION
##################################################


def gabor_norm(s, t0, f0, phase):
    """
    Calculates a correct normalization factor for Gabor atom.
    """
    nyquist_frequency = 0.5 * sampling_frequency
    if f0 > 0.5 * nyquist_frequency:
        f0 = nyquist_frequency - f0
        phase = 2 * numpy.pi * nyquist_frequency * t0 - phase
    return numpy.sqrt(2**1.5 / (
        s * (1 + numpy.cos(2*phase) * numpy.exp(-2*numpy.pi*s*s*f0*f0))
    ))


def gabor(t, s, t0, f0, phase=0.0):
    """
    Generates values for Gabor atom normalized to unit energy.
    """
    return gabor_norm(s, t0, f0, phase) \
        * numpy.exp(-numpy.pi*((t-t0)/s)**2) \
        * numpy.cos(2*numpy.pi*f0*(t-t0) + phase)


if __name__ == '__main__':
    numpy.random.seed(42)  # for deterministic results

    # shape of the resulting signal
    channel_count = 5
    sample_count = 5120

    t = numpy.arange(sample_count) / sampling_frequency
    data = numpy.zeros((channel_count, sample_count))
    for i in range(channel_count):
        data[i] = 10 * gabor(t, s=3.0, t0=5.0, f0=i+1) + 0.1 * numpy.random.randn(sample_count)

    # save to a single binary file with interleaved channels
    data.T.reshape((-1)).astype('float32').tofile('input.bin')

    if len(sys.argv) > 1 and sys.argv[1] == '--plot':
        # optionally, plot all channels
        import pylab
        for i in range(channel_count):
            pylab.subplot(channel_count, 1, 1+i)
            pylab.plot(t, data[i])
        pylab.show()
