################################
# MATCHING PURSUIT DEMO TOOLKIT
################################

This toolkit demonstrates the usage of JSON decomposition files generated
with matching pursuit implementation «empi» in Python and Matlab.

The main part consists of two scripts: «demo_mp.py» and «demo_mp.m».

Both scripts read the file consisting of matching pursuit decomposition
«input_smp.db», plotting the original signal and its reconstruction,
as well as printing the atoms' parameters to the standard output.
Atom filtering may be enabled by un-commenting parts of the source code.

Additional files allow to understand the procedure in more detail:

1. Example input signal file «input.bin» consist of a synthetic
multi-channel signal with one Gabor atom per channel, and can be
re-generated with a Python script «input_generator.py». The script accepts
an optional "--plot" command line parameter to plot the generated signal.

2. Decomposition results «input_smp.db» can be computed by running empi
with the given configuration file «input.cfg» as

  path/to/empi input.cfg

Thanks to the attached «input.xml» specification, the input signal itself
may be displayed and manipulated in Svarog as well.
