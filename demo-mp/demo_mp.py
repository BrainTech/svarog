#!/usr/bin/env python
import numpy
import pylab
import sqlite3
import sys

##################################################
# PLEASE REFER TO README.txt FOR MORE INFORMATION
##################################################


def demo_mp():
    cursor = sqlite3.connect('input_smp.db').cursor()
    cursor.row_factory = sqlite3.Row

    # number of channels for each segment
    channel_count = int(fetch_metadata(cursor, 'channel_count'))

    # sampling frequency in hertz
    sampling_frequency = float(fetch_metadata(cursor, 'sampling_frequency_Hz'))

    # segment_id (starting from 0) can be given as command line parameter;
    # it does not matter for the example file as there is only one segment
    segment_id = int(sys.argv[1]) if len(sys.argv) > 1 else 0

    # number of samples in segment
    sample_count = fetch_sample_count(cursor, segment_id)

    for channel_id in range(channel_count):
        # each channel forms a separate sub-plot
        pylab.subplot(channel_count, 1, 1+channel_id)
        original_signal = fetch_original_signal(cursor, segment_id, channel_id)
        reconstruction = numpy.zeros(sample_count)

        t = numpy.arange(sample_count) / sampling_frequency
        query_for_atoms(cursor, segment_id, channel_id) # results will be iterated through cursor
        for atom in cursor:
            if atom['envelope'] != 'gauss':
                raise Exception('only Gabor atoms are supported')

            amplitude = atom['amplitude']
            energy = atom['energy']
            f = atom['f_Hz']  # frequency in hertz
            phase = atom['phase']  # phase in radians
            s = atom['scale_s']  # scale in seconds
            t0 = atom['t0_s']  # position (centre) in seconds
            t0_abs = atom['t0_abs_s']  # absolute position

            # lines below may be un-commented and edited to exclude
            # selected atoms from reconstruction
            '''
            if f < 2.5:
                continue
            '''
            g = amplitude * gabor(t, s, t0, f, phase)
            energy = numpy.sum(g**2) / sampling_frequency
            reconstruction += g
            print('\n-- ATOM IN CHANNEL {} --'.format(channel_id))
            print('amplitude = %.3f' % amplitude)
            print('scale = %.3f s' % s)
            print('position in segment = %.3f s' % t0)
            print('position in signal = %.3f s' % t0_abs)
            print('frequency = %.3f Hz' % f)
            print('energy = %.6f' % energy)

        pylab.plot(t, original_signal)
        pylab.plot(t, reconstruction, 'red')

    pylab.show()


def gabor(t, s, t0, f0, phase=0.0):
    """
    Generates values for Gabor atom with unit amplitude.
    """
    return numpy.exp(-numpy.pi*((t-t0)/s)**2) \
        * numpy.cos(2*numpy.pi*f0*(t-t0) + phase)


def fetch_metadata(cursor, name):
    return cursor.execute(
        'SELECT value FROM metadata WHERE param=?',
        [name]
    ).fetchone()[0]


def fetch_sample_count(cursor, segment_id):
    return cursor.execute(
        'SELECT sample_count FROM segments WHERE segment_id=?',
        [segment_id]
    ).fetchone()[0]


def fetch_original_signal(cursor, segment_id, channel_id):
    blob = cursor.execute(
        'SELECT samples_float32 FROM samples WHERE segment_id=? AND channel_id=?',
        [segment_id, channel_id]
    ).fetchone()[0]
    return numpy.frombuffer(blob, numpy.dtype('float32').newbyteorder('>'))


def query_for_atoms(cursor, segment_id, channel_id):
    cursor.execute(
        'SELECT * FROM atoms WHERE segment_id=? AND channel_id=? ORDER BY iteration',
        [segment_id, channel_id]
    )


if __name__ == '__main__':
    demo_mp()
