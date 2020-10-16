%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% PLEASE REFER TO README.txt FOR MORE INFORMATION
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function [] = demo_mp(segment_id)
    % segment_id (starting from 0) can be given as a parameter;
    % it does not matter for the example file as there is only one segment
    if nargin < 1
        segment_id = 0;
    end

    % for Octave or legacy Matlab versions you need to download sqlite4java
    % (jar + dynamic library), put it in this directory,
    % and uncomment the following line:
    %javaaddpath(['sqlite4java.jar']);

    file = javaObject('java.io.File', 'input_smp.db');
    db = javaObject('com.almworks.sqlite4java.SQLiteConnection', file);
    db.open(false);

    % number of channels for each segment
    channel_count = fetch_metadata_int(db, 'channel_count');

    % sampling frequency in hertz
    sampling_frequency = fetch_metadata_float(db, 'sampling_frequency_Hz');

    % number of samples in segment
    sample_count = fetch_sample_count(db, segment_id);

    for channel_id=(0:channel_count-1)
        % each channel forms a separate sub-plot
        subplot(channel_count, 1, 1+channel_id);
        original_signal = fetch_original_signal(db, segment_id, channel_id, sample_count);
        reconstruction = zeros(1, sample_count);

        t = (0:sample_count-1) / sampling_frequency;
        stmt = query_for_atoms(db, segment_id, channel_id);
        while stmt.step()
            envelope = stmt.columnString(2);
            if strcmp(envelope, 'gauss') == 0
                error('only Gabor atoms are supported');
            end

            amplitude = stmt.columnDouble(0);
            energy = stmt.columnDouble(1);
            f = stmt.columnDouble(3);  % frequency in hertz
            phase = stmt.columnDouble(4);  % phase in radians
            s = stmt.columnDouble(5);  % scale in seconds
            t0 = stmt.columnDouble(6);  % position (centre) in seconds
            t0_abs = stmt.columnDouble(7);  % absolute position

            % lines below may be un-commented and edited to exclude
            % selected atoms from reconstruction
            %if f < 2.5
            %continue;
            %end

            g = amplitude * gabor(t, s, t0, f, phase);
            energy = sum(g.^2) / sampling_frequency;
            reconstruction = reconstruction + g;
            disp(sprintf('\n-- ATOM IN CHANNEL %d --', channel_id));
            disp(sprintf('amplitude = %.3f', amplitude));
            disp(sprintf('scale = %.3f s', s));
            disp(sprintf('position in segment = %.3f s', t0));
            disp(sprintf('position in signal = %.3f s', t0_abs));
            disp(sprintf('frequency = %.3f Hz', f));
            disp(sprintf('energy = %.6f', energy));
        end
        stmt.dispose();

        plot(t, original_signal, t, reconstruction, 'r')
    end

    db.dispose();
end

function [g] = gabor(t, s, t0, f0, phase)
    % Generates values for Gabor atom with unit amplitude.
    if nargin < 5
        phase = 0.0;
    end
    g = exp(-pi*((t-t0)/s).^2) .* cos(2*pi*f0*(t-t0) + phase);
end

function [result] = fetch_metadata_int(db, param)
    stmt = db.prepare('SELECT value FROM metadata WHERE param=?');
    stmt.bind(1, param);
    stmt.step();
    result = stmt.columnInt(0);
    stmt.dispose();
end

function [result] = fetch_metadata_float(db, param)
    stmt = db.prepare('SELECT value FROM metadata WHERE param=?');
    stmt.bind(1, param);
    stmt.step();
    result = stmt.columnDouble(0);
    stmt.dispose();
end

function [result] = fetch_sample_count(db, segment_id)
    stmt = db.prepare('SELECT sample_count FROM segments WHERE segment_id=?');
    stmt.bind(1, segment_id);
    stmt.step();
    result = stmt.columnInt(0);
    stmt.dispose();
end

function [result] = fetch_original_signal(db, segment_id, channel_id, sample_count)
    stmt = db.prepare('SELECT samples_float32 FROM samples WHERE segment_id=? AND channel_id=?');
    stmt.bind(1, segment_id);
    stmt.bind(2, channel_id);
    stmt.step();
    stream = javaObject('java.io.DataInputStream', stmt.columnStream(0));
    result = zeros(1, sample_count);
    for i=(1:sample_count)
        result(i) = stream.readFloat();
    end
    stream.close();
    stmt.dispose();
end

function [stmt] = query_for_atoms(db, segment_id, channel_id)
    stmt = db.prepare('SELECT amplitude, energy, envelope, f_Hz, phase, scale_s, t0_s, t0_abs_s FROM atoms WHERE segment_id=? AND channel_id=? ORDER BY iteration');
    stmt.bind(1, segment_id);
    stmt.bind(2, channel_id);
end
