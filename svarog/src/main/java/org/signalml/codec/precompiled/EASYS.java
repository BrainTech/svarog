package org.signalml.codec.precompiled;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import jsignalml.BitForm;
import jsignalml.Builtins;
import jsignalml.Channel;
import jsignalml.ChannelSet;
import jsignalml.MyBuffer;
import jsignalml.Type;
import jsignalml.TypeFloat;
import jsignalml.TypeInt;
import jsignalml.TypeList;
import jsignalml.TypeMap;
import jsignalml.TypeString;
import jsignalml.codec.ChannelClass;
import jsignalml.codec.ChannelSetClass;
import jsignalml.codec.CodecId;
import jsignalml.codec.ConditionalClass;
import jsignalml.codec.Context;
import jsignalml.codec.FormatId;
import jsignalml.codec.FunctionParam;
import jsignalml.codec.Header;
import jsignalml.codec.OuterLoopClass;
import jsignalml.codec.Param;
import jsignalml.codec.Signalml;
import jsignalml.logging.Logger;
import org.apache.log4j.BasicConfigurator;


/**
 * 
 * jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
 * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
 * jsignalml.JavaClassGen.visit(JavaClassGen.java:185)
 * 
 */
public class EASYS
    extends Signalml
{

    final static Logger log = new Logger(EASYS.class);
    private int channelCounter = 0;
    EASYS.header get_header = null;
    EASYS.File_main get_main = null;

    public void createParams() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:190)
        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
        log.debug("EASYS.createParams()");
        {
            // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
            // jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1144)
            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            EASYS.header obj = get_header();
            register("header", obj);
            obj.createParams();
        }
        {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1232)
            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            EASYS.File_main obj = get_main();
            register("main", obj);
            obj.createParams();
        }
    }

    public void createChannels() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:190)
        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
        log.debug("EASYS.createChannels()");
        {
            // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
            // jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1144)
            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            EASYS.header obj = get_header();
            obj.createChannels();
        }
        {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1232)
            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            EASYS.File_main obj = get_main();
            obj.createChannels();
        }
    }

    public java.lang.String id() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:193)
        // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
        return "EASYS";
    }

    public static void main(java.lang.String... args) {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:194)
        // jsignalml.JavaClassGen.mainMethod(JavaClassGen.java:313)
        int argc = args.length;
        if (argc< 1) {
            System.out.println("Syntax:\n\tEASYS inputFile channelNr1 channelNr2 ...");
            return ;
        }
        BasicConfigurator.configure();
        EASYS reader = new EASYS();
        reader.open(new File(args[ 0 ]));
        reader.createParams();
        reader.createChannels();
        // System.out.print(ContextDumper.dump(reader));
        int nrOfChannelSets = reader.getNumberOfChannelSets();
        for (int k = 0; (k<nrOfChannelSets); k ++) {
            ChannelSet channelSet = reader.get_set(k);
            int nrOfChannels = channelSet.getNumberOfChannels();
            int nrOfChannelsToShow = nrOfChannels;
            System.out.println(("Input file for EASYS codec: "+ args[ 0 ]));
            System.out.println((("Input file has "+ nrOfChannels)+" channels"));
            if (argc > 1) {
                nrOfChannelsToShow = argc;
            }
            for (int j = 1; (j<= nrOfChannelsToShow); j ++) {
                int channelNr = (j- 1);
                if (argc > 1) {
                    channelNr = Integer.decode(args[j]).intValue();
                }
                Channel channel = channelSet.getChannel(channelNr);
                int nrOfSamples = ((int) channel.getNumberOfSamples());
                int nrOfSamplesToShow = Math.min(nrOfSamples, 10);
                java.lang.String channelName = channel.getChannelName();
                java.lang.String channelType = channel.getChannelType();
                System.out.println(((("Channel #"+ channelNr)+(("["+ channelType)+(" "+ channelName)))+(("] has "+ nrOfSamples)+" samples:")));
                for (int sampleNr = 0; (sampleNr<nrOfSamplesToShow); sampleNr ++) {
                    float sampleUnitValue = channel.getSample(sampleNr);
                    System.out.println((("\tSample #"+ sampleNr)+(" ---> "+ sampleUnitValue)));
                }
            }
        }
    }

    public File getCurrentFilename() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:195)
        // jsignalml.JavaClassGen.getCurrentFilenameMethod(JavaClassGen.java:437)
        return null;
    }

    public java.lang.String getFormatDescription() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:196)
        // jsignalml.JavaClassGen.getFormatDescriptionMethod(JavaClassGen.java:446)
        return null;
    }

    public java.lang.String getFormatID() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:197)
        // jsignalml.JavaClassGen.getFormatIDMethod(JavaClassGen.java:455)
        return null;
    }

    public void open(File filename) {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:198)
        // jsignalml.JavaClassGen.codecOpenMethod(JavaClassGen.java:427)
        this.default_filename = filename;
    }

    public void close() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:199)
        // jsignalml.JavaClassGen.closeMethod(JavaClassGen.java:539)
    }

    public java.lang.String getFormatName() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:200)
        // jsignalml.JavaClassGen.getFormatNameMethod(JavaClassGen.java:464)
        get_header();
        java.lang.String formatName = get_header().get_format_id().name.get().toString();
        return formatName;
    }

    public java.lang.String getFormatProvider() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:201)
        // jsignalml.JavaClassGen.getFormatProviderMethod(JavaClassGen.java:479)
        get_header();
        java.lang.String formatProvider = get_header().get_format_id().provider.get().toString();
        return formatProvider;
    }

    public java.lang.String getFormatVersion() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:202)
        // jsignalml.JavaClassGen.getFormatVersionMethod(JavaClassGen.java:494)
        get_header();
        java.lang.String formatVersion = get_header().get_format_id().version.get().toString();
        return formatVersion;
    }

    public java.lang.String getCodecProvider() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:203)
        // jsignalml.JavaClassGen.getCodecProviderMethod(JavaClassGen.java:509)
        get_header();
        java.lang.String codecProvider = get_header().get_codec_id().provider.get().toString();
        return codecProvider;
    }

    public java.lang.String getCodecVersion() {
        // jsignalml.ASTNode$Signalml._accept(ASTNode.java:123)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:204)
        // jsignalml.JavaClassGen.getCodecVersionMethod(JavaClassGen.java:524)
        get_header();
        java.lang.String codecVersion = get_header().get_codec_id().version.get().toString();
        return codecVersion;
    }

    public EASYS.header get_header() {
        // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
        // jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1141)
        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
        if (get_header == null) {
            get_header = new EASYS.header();
        }
        return get_header;
    }

    public EASYS.File_main get_main() {
        // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
        // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1229)
        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
        if (get_main == null) {
            get_main = new EASYS.File_main();
        }
        return get_main;
    }


    /**
     * 
     * jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
     * jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1170)
     * parent paramClass=_param_mapping
     * parent paramClass=_param_signature
     * parent paramClass=_param_magic
     * parent paramClass=_param_file_type
     * parent paramClass=_param_number_of_channels
     * parent paramClass=_param_number_of_auxiliary_channels
     * parent paramClass=_param_sampling_frequency
     * parent paramClass=_param_number_of_samples
     * parent paramClass=_param_data_validation_field
     * parent paramClass=_param_data_cell_size
     * parent paramClass=_param_unit
     * parent paramClass=_param__calibration_unit
     * parent paramClass=_param_calibration_gain
     * parent paramClass=_param_calibration_offset
     * parent paramClass=_param_data_org
     * parent paramClass=_param_data_offset
     * parent paramClass=_param_xhdr_org
     * parent paramClass=_param_extended_header_offset
     * parent paramClass=_param_record_name_map
     * 
     */
    public class File_main
        extends Signalml.FileClass
    {

        EASYS.File_main._param_mapping get_mapping = null;
        EASYS.File_main._param_signature get_signature = null;
        EASYS.File_main._param_magic get_magic = null;
        EASYS.File_main._param_file_type get_file_type = null;
        EASYS.File_main._param_number_of_channels get_number_of_channels = null;
        EASYS.File_main._param_number_of_auxiliary_channels get_number_of_auxiliary_channels = null;
        EASYS.File_main._param_sampling_frequency get_sampling_frequency = null;
        EASYS.File_main._param_number_of_samples get_number_of_samples = null;
        EASYS.File_main._param_data_validation_field get_data_validation_field = null;
        EASYS.File_main._param_data_cell_size get_data_cell_size = null;
        EASYS.File_main._param_unit get_unit = null;
        EASYS.File_main._param__calibration_unit get__calibration_unit = null;
        EASYS.File_main._param_calibration_gain get_calibration_gain = null;
        EASYS.File_main._param_calibration_offset get_calibration_offset = null;
        EASYS.File_main._param_data_org get_data_org = null;
        EASYS.File_main._param_data_offset get_data_offset = null;
        EASYS.File_main._param_xhdr_org get_xhdr_org = null;
        EASYS.File_main._param_extended_header_offset get_extended_header_offset = null;
        EASYS.File_main._param_record_name_map get_record_name_map = null;
        EASYS.File_main.ChannelSet_data get_data = null;

        public File_main() {
        }

        public Type access(java.lang.String name) {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1211)
            return super.access(name);
        }

        public void register(java.lang.String name, Context child) {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1219)
            super.register(name, child);
        }

        public void createParams() {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1226)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            log.debug("File_main.createParams()");
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("mapping", get_mapping());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("signature", get_signature());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("magic", get_magic());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("file_type", get_file_type());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("number_of_channels", get_number_of_channels());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("number_of_auxiliary_channels", get_number_of_auxiliary_channels());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("sampling_frequency", get_sampling_frequency());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("number_of_samples", get_number_of_samples());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("data_validation_field", get_data_validation_field());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("data_cell_size", get_data_cell_size());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("unit", get_unit());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("_calibration_unit", get__calibration_unit());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("calibration_gain", get_calibration_gain());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("calibration_offset", get_calibration_offset());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("data_org", get_data_org());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("data_offset", get_data_offset());
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("xhdr_org", get_xhdr_org());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("extended_header_offset", get_extended_header_offset());
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("record_name_map", get_record_name_map());
            {
                // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
                // jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1656)
                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                EASYS.File_main.ChannelSet_data obj = get_data();
                register("data", obj);
                obj.createParams();
            }
        }

        public void createChannels() {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1155)
            // jsignalml.JavaClassGen.fileClass(JavaClassGen.java:1226)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            log.debug("File_main.createChannels()");
            {
                // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
                // jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1656)
                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                EASYS.File_main.ChannelSet_data obj = get_data();
                obj.createChannels();
            }
            registerChannelSet(get_data());
        }

        public java.lang.String id() {
            // jsignalml.ASTNode$FileHandle._accept(ASTNode.java:457)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1156)
            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
            return "main";
        }

        public EASYS.File_main._param_mapping get_mapping() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_mapping == null) {
                get_mapping = new EASYS.File_main._param_mapping();
            }
            return get_mapping;
        }

        public EASYS.File_main._param_signature get_signature() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_signature == null) {
                get_signature = new EASYS.File_main._param_signature();
            }
            return get_signature;
        }

        public EASYS.File_main._param_magic get_magic() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_magic == null) {
                get_magic = new EASYS.File_main._param_magic();
            }
            return get_magic;
        }

        public EASYS.File_main._param_file_type get_file_type() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_file_type == null) {
                get_file_type = new EASYS.File_main._param_file_type();
            }
            return get_file_type;
        }

        public EASYS.File_main._param_number_of_channels get_number_of_channels() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_number_of_channels == null) {
                get_number_of_channels = new EASYS.File_main._param_number_of_channels();
            }
            return get_number_of_channels;
        }

        public EASYS.File_main._param_number_of_auxiliary_channels get_number_of_auxiliary_channels() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_number_of_auxiliary_channels == null) {
                get_number_of_auxiliary_channels = new EASYS.File_main._param_number_of_auxiliary_channels();
            }
            return get_number_of_auxiliary_channels;
        }

        public EASYS.File_main._param_sampling_frequency get_sampling_frequency() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_sampling_frequency == null) {
                get_sampling_frequency = new EASYS.File_main._param_sampling_frequency();
            }
            return get_sampling_frequency;
        }

        public EASYS.File_main._param_number_of_samples get_number_of_samples() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_number_of_samples == null) {
                get_number_of_samples = new EASYS.File_main._param_number_of_samples();
            }
            return get_number_of_samples;
        }

        public EASYS.File_main._param_data_validation_field get_data_validation_field() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_data_validation_field == null) {
                get_data_validation_field = new EASYS.File_main._param_data_validation_field();
            }
            return get_data_validation_field;
        }

        public EASYS.File_main._param_data_cell_size get_data_cell_size() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_data_cell_size == null) {
                get_data_cell_size = new EASYS.File_main._param_data_cell_size();
            }
            return get_data_cell_size;
        }

        public EASYS.File_main._param_unit get_unit() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_unit == null) {
                get_unit = new EASYS.File_main._param_unit();
            }
            return get_unit;
        }

        public EASYS.File_main._param__calibration_unit get__calibration_unit() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get__calibration_unit == null) {
                get__calibration_unit = new EASYS.File_main._param__calibration_unit();
            }
            return get__calibration_unit;
        }

        public EASYS.File_main._param_calibration_gain get_calibration_gain() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_calibration_gain == null) {
                get_calibration_gain = new EASYS.File_main._param_calibration_gain();
            }
            return get_calibration_gain;
        }

        public EASYS.File_main._param_calibration_offset get_calibration_offset() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_calibration_offset == null) {
                get_calibration_offset = new EASYS.File_main._param_calibration_offset();
            }
            return get_calibration_offset;
        }

        public EASYS.File_main._param_data_org get_data_org() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_data_org == null) {
                get_data_org = new EASYS.File_main._param_data_org();
            }
            return get_data_org;
        }

        public EASYS.File_main._param_data_offset get_data_offset() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_data_offset == null) {
                get_data_offset = new EASYS.File_main._param_data_offset();
            }
            return get_data_offset;
        }

        public EASYS.File_main._param_xhdr_org get_xhdr_org() {
            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_xhdr_org == null) {
                get_xhdr_org = new EASYS.File_main._param_xhdr_org();
            }
            return get_xhdr_org;
        }

        public EASYS.File_main._param_extended_header_offset get_extended_header_offset() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_extended_header_offset == null) {
                get_extended_header_offset = new EASYS.File_main._param_extended_header_offset();
            }
            return get_extended_header_offset;
        }

        public EASYS.File_main._param_record_name_map get_record_name_map() {
            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_record_name_map == null) {
                get_record_name_map = new EASYS.File_main._param_record_name_map();
            }
            return get_record_name_map;
        }

        public EASYS.File_main.ChannelSet_data get_data() {
            // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
            // jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1653)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_data == null) {
                get_data = new EASYS.File_main.ChannelSet_data();
            }
            return get_data;
        }


        /**
         * 
         * jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
         * jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1648)
         * jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1634)
         * 
         */
        public class ChannelSet_data
            extends ChannelSetClass
        {

            EASYS.File_main.ChannelSet_data.Loop_extended_header get_extended_header = null;

            public void createParams() {
                // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
                // jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1650)
                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                log.debug("ChannelSet_data.createParams()");
                {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1364)
                    // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                    EASYS.File_main.ChannelSet_data.Loop_extended_header obj = get_extended_header();
                    register("extended_header", obj);
                    obj.createParams();
                }
            }

            public void createChannels() {
                // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1633)
                // jsignalml.JavaClassGen.channelSetClass(JavaClassGen.java:1650)
                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                log.debug("ChannelSet_data.createChannels()");
                {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1364)
                    // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                    EASYS.File_main.ChannelSet_data.Loop_extended_header obj = get_extended_header();
                    obj.createChannels();
                    obj.createLoopChannels();
                }
            }

            public java.lang.String id() {
                // jsignalml.ASTNode$ChannelSet._accept(ASTNode.java:142)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1635)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "data";
            }

            public EASYS.File_main.ChannelSet_data.Loop_extended_header get_extended_header() {
                // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1360)
                // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                if (get_extended_header == null) {
                    get_extended_header = new EASYS.File_main.ChannelSet_data.Loop_extended_header();
                }
                return get_extended_header;
                // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1361)
            }


            /**
             * 
             * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
             * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
             * jsignalml.JavaClassGen.visit(JavaClassGen.java:1336)
             * 
             */
            public class Loop_extended_header
                extends OuterLoopClass
            {


                public void createParams() {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1357)
                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                    log.debug("Loop_extended_header.createParams()");
                }

                public void createChannels() {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1357)
                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                    log.debug("Loop_extended_header.createChannels()");
                }

                public java.lang.String id() {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1337)
                    // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                    return "extended_header";
                }

                protected TypeList getSequence() {
                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1338)
                    // jsignalml.JavaClassGen.sequenceMethod(JavaClassGen.java:1373)
                    TypeList range = ((TypeList)(get_xhdr_org().get().isTrue()?Builtins.range().call(new TypeInt(20)):new TypeList()));
                    return range;
                }

                protected EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner createLoop(Type index) {
                    // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                    // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1601)
                    // jsignalml.JavaClassGen.createLoopMethod(JavaClassGen.java:1390)
                    return new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner(((TypeInt) index));
                }


                /**
                 * 
                 * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                 * jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1409)
                 * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:1341)
                 * 
                 */
                public class extended_header_inner
                    extends OuterLoopClass.LoopClass
                {

                    final EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.indexHdr index;
                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr get_if_ext_hdr = null;

                    extended_header_inner(TypeInt indexHdr) {
                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                        // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1599)
                        // jsignalml.JavaClassGen.loopClassConstructor(JavaClassGen.java:1619)
                        this.index = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.indexHdr(indexHdr);
                    }

                    public void createParams() {
                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                        // jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1411)
                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                        log.debug("extended_header_inner.createParams()");
                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1315)
                        // jsignalml.JavaClassGen.iternameGetter(JavaClassGen.java:1326)
                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                        register("indexHdr", this.index);
                        {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1459)
                            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr obj = get_if_ext_hdr();
                            register("if_ext_hdr", obj);
                            obj.createParams();
                        }
                    }

                    public void createChannels() {
                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                        // jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1411)
                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                        log.debug("extended_header_inner.createChannels()");
                        {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1459)
                            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr obj = get_if_ext_hdr();
                            obj.createChannels();
                        }
                    }

                    public java.lang.String id() {
                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1342)
                        // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                        return "extended_header";
                    }

                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.indexHdr get_indexHdr() {
                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1315)
                        // jsignalml.JavaClassGen.iternameGetter(JavaClassGen.java:1322)
                        return this.index;
                    }

                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr get_if_ext_hdr() {
                        // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                        // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1451)
                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                        if (get_if_ext_hdr == null) {
                            get_if_ext_hdr = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr();
                        }
                        return get_if_ext_hdr;
                    }


                    /**
                     * 
                     * jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                     * jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1446)
                     * jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1431)
                     * parent paramClass=_param_offset
                     * parent paramClass=_param_record_mnemonic
                     * parent paramClass=_param_record_size
                     * 
                     */
                    public class If_if_ext_hdr
                        extends ConditionalClass
                    {

                        EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_offset get_offset = null;
                        EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_mnemonic get_record_mnemonic = null;
                        EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_size get_record_size = null;
                        EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names get_if_record_channel_names = null;

                        public void createParamsIf() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:303)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createParamsIf()");
                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            register("offset", get_offset());
                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            register("record_mnemonic", get_record_mnemonic());
                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            register("record_size", get_record_size());
                            {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1459)
                                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names obj = get_if_record_channel_names();
                                register("if_record_channel_names", obj);
                                obj.createParams();
                            }
                        }

                        public void createChannelsIf() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:303)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createChannelsIf()");
                            {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1459)
                                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names obj = get_if_record_channel_names();
                                obj.createChannels();
                            }
                        }

                        public void createParamsElseIf() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:304)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createParamsElseIf()");
                        }

                        public void createChannelsElseIf() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:304)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createChannelsElseIf()");
                        }

                        public void createParamsElse() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:305)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createParamsElse()");
                            {
                                // jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1485)
                                // jsignalml.JavaClassGen.elseBranchClass(JavaClassGen.java:1501)
                                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1 obj = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1();
                                register("gen_id_1", obj);
                                obj.createParams();
                            }
                        }

                        public void createChannelsElse() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                            // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:305)
                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                            log.debug("If_if_ext_hdr.createChannelsElse()");
                            {
                                // jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1485)
                                // jsignalml.JavaClassGen.elseBranchClass(JavaClassGen.java:1501)
                                // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1 obj = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1();
                                obj.createChannels();
                            }
                        }

                        public boolean hasElseIf() {
                            return false;
                        }

                        public java.lang.String id() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1432)
                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                            return "if_ext_hdr";
                        }

                        public Type getCondition() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1433)
                            // jsignalml.JavaClassGen.conditionMethod(JavaClassGen.java:1472)
                            Type test = (((get_indexHdr().get().compareTo(new TypeInt(0)) == 0)?TypeInt.True:TypeInt.False).isTrue()?((get_indexHdr().get().compareTo(new TypeInt(0)) == 0)?TypeInt.True:TypeInt.False):get_extended_header().get().index(get_indexHdr().get().sub(new TypeInt(1))).access("if_ext_hdr").access("record_size"));
                            return test;
                        }

                        public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_offset get_offset() {
                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                            if (get_offset == null) {
                                get_offset = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_offset();
                            }
                            return get_offset;
                        }

                        public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_mnemonic get_record_mnemonic() {
                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                            if (get_record_mnemonic == null) {
                                get_record_mnemonic = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_mnemonic();
                            }
                            return get_record_mnemonic;
                        }

                        public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_size get_record_size() {
                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                            // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                            if (get_record_size == null) {
                                get_record_size = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr._param_record_size();
                            }
                            return get_record_size;
                        }

                        public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names get_if_record_channel_names() {
                            // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                            // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1451)
                            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                            if (get_if_record_channel_names == null) {
                                get_if_record_channel_names = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names();
                            }
                            return get_if_record_channel_names;
                        }


                        /**
                         * 
                         * jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1485)
                         * jsignalml.JavaClassGen.elseBranchClass(JavaClassGen.java:1503)
                         * jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1486)
                         * parent paramClass=_param_record_size
                         * 
                         */
                        public class Else_gen_id_1
                            extends ConditionalClass.ElseBranchClass
                        {

                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1 ._param_record_size get_record_size = null;

                            public void createParams() {
                                // jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1485)
                                // jsignalml.JavaClassGen.elseBranchClass(JavaClassGen.java:1504)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("Else_gen_id_1.createParams()");
                                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                                // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                register("record_size", get_record_size());
                            }

                            public void createChannels() {
                                // jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1485)
                                // jsignalml.JavaClassGen.elseBranchClass(JavaClassGen.java:1504)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("Else_gen_id_1.createChannels()");
                            }

                            public java.lang.String id() {
                                // jsignalml.ASTNode$ElseBranch._accept(ASTNode.java:675)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1487)
                                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                return "gen_id_1";
                            }

                            public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1 ._param_record_size get_record_size() {
                                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                                // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                if (get_record_size == null) {
                                    get_record_size = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.Else_gen_id_1 ._param_record_size();
                                }
                                return get_record_size;
                            }


                            /**
                             * 
                             * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                             * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                             * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                             * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                             * node.type=TypeInt
                             * --> nodetype=TypeInt
                             * 
                             */
                            public class _param_record_size
                                extends Param<TypeInt>
                            {


                                public java.lang.String id() {
                                    // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                                    // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                    return "record_size";
                                }

                                protected TypeInt _get() {
                                    // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                                    // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                                    // node.type=TypeInt
                                    // node.expr.type=TypeInt
                                    // --> nodetype=TypeInt
                                    return ((TypeInt) new TypeInt(0));
                                }

                            }

                        }


                        /**
                         * 
                         * jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                         * jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1446)
                         * jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:1431)
                         * 
                         */
                        public class If_if_record_channel_names
                            extends ConditionalClass
                        {

                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels get_channels = null;

                            public void createParamsIf() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:303)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createParamsIf()");
                                {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1364)
                                    // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels obj = get_channels();
                                    register("channels", obj);
                                    obj.createParams();
                                }
                            }

                            public void createChannelsIf() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:303)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createChannelsIf()");
                                {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1364)
                                    // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels obj = get_channels();
                                    obj.createChannels();
                                    obj.createLoopChannels();
                                }
                            }

                            public void createParamsElseIf() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:304)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createParamsElseIf()");
                            }

                            public void createChannelsElseIf() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:304)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createChannelsElseIf()");
                            }

                            public void createParamsElse() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:305)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createParamsElse()");
                            }

                            public void createChannelsElse() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1430)
                                // jsignalml.JavaClassGen.conditionalClass(JavaClassGen.java:1448)
                                // jsignalml.JavaClassGen$MetadataIfBranch.<init>(JavaClassGen.java:305)
                                // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                log.debug("If_if_record_channel_names.createChannelsElse()");
                            }

                            public boolean hasElseIf() {
                                return false;
                            }

                            public java.lang.String id() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1432)
                                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                return "if_record_channel_names";
                            }

                            public Type getCondition() {
                                // jsignalml.ASTNode$Conditional._accept(ASTNode.java:602)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1433)
                                // jsignalml.JavaClassGen.conditionMethod(JavaClassGen.java:1472)
                                Type test = ((get_record_mnemonic().get().compareTo(new TypeString("CN")) == 0)?TypeInt.True:TypeInt.False);
                                return test;
                            }

                            public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels get_channels() {
                                // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1360)
                                // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                if (get_channels == null) {
                                    get_channels = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels();
                                }
                                return get_channels;
                                // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1361)
                            }


                            /**
                             * 
                             * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                             * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                             * jsignalml.JavaClassGen.visit(JavaClassGen.java:1336)
                             * 
                             */
                            public class Loop_channels
                                extends OuterLoopClass
                            {


                                public void createParams() {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1357)
                                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                    log.debug("Loop_channels.createParams()");
                                }

                                public void createChannels() {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1335)
                                    // jsignalml.JavaClassGen.outerLoopClass(JavaClassGen.java:1357)
                                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                    // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                    // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                    log.debug("Loop_channels.createChannels()");
                                }

                                public java.lang.String id() {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1337)
                                    // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                    return "channels";
                                }

                                protected TypeList getSequence() {
                                    // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1338)
                                    // jsignalml.JavaClassGen.sequenceMethod(JavaClassGen.java:1373)
                                    TypeList range = ((TypeList) Builtins.range().call(get_number_of_channels().get()));
                                    return range;
                                }

                                protected EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner createLoop(Type index) {
                                    // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                    // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                    // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1601)
                                    // jsignalml.JavaClassGen.createLoopMethod(JavaClassGen.java:1390)
                                    return new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner(((TypeInt) index));
                                }


                                /**
                                 * 
                                 * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                                 * jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1409)
                                 * jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                 * jsignalml.JavaClassGen.visit(JavaClassGen.java:1341)
                                 * parent paramClass=_param_channel_name_ASCIIZ
                                 * parent paramClass=_param_channel_name
                                 * parent paramClass=_param_channel_type
                                 * parent paramClass=_param_mapping
                                 * 
                                 */
                                public class channels_inner
                                    extends OuterLoopClass.LoopClass
                                {

                                    final EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.index index;
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.Channel_gen_id_0 get_gen_id_0 = null;
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name_ASCIIZ get_channel_name_ASCIIZ = null;
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name get_channel_name = null;
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_type get_channel_type = null;
                                    EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_mapping get_mapping = null;

                                    channels_inner(TypeInt index) {
                                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                        // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1599)
                                        // jsignalml.JavaClassGen.loopClassConstructor(JavaClassGen.java:1619)
                                        this.index = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.index(index);
                                    }

                                    public void createParams() {
                                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                                        // jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1411)
                                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        log.debug("channels_inner.createParams()");
                                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1315)
                                        // jsignalml.JavaClassGen.iternameGetter(JavaClassGen.java:1326)
                                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        register("index", this.index);
                                        {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                            // jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1704)
                                            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:264)
                                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.Channel_gen_id_0 obj = get_gen_id_0();
                                            register("gen_id_0", obj);
                                            obj.createParams();
                                        }
                                        // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        register("channel_name_ASCIIZ", get_channel_name_ASCIIZ());
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        register("channel_name", get_channel_name());
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        register("channel_type", get_channel_type());
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:621)
                                        // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        register("mapping", get_mapping());
                                    }

                                    public void createChannels() {
                                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1340)
                                        // jsignalml.JavaClassGen.loopClass(JavaClassGen.java:1411)
                                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                        // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                        // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                        log.debug("channels_inner.createChannels()");
                                        {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                            // jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1704)
                                            // jsignalml.JavaClassGen$Metadata.registerContext(JavaClassGen.java:271)
                                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                            EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.Channel_gen_id_0 obj = get_gen_id_0();
                                            obj.createChannels();
                                        }
                                        registerChannel(get_gen_id_0());
                                    }

                                    public java.lang.String id() {
                                        // jsignalml.ASTNode$ForLoop._accept(ASTNode.java:548)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1342)
                                        // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                        return "channels";
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.index get_index() {
                                        // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1315)
                                        // jsignalml.JavaClassGen.iternameGetter(JavaClassGen.java:1322)
                                        return this.index;
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.Channel_gen_id_0 get_gen_id_0() {
                                        // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                        // jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1701)
                                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                        if (get_gen_id_0 == null) {
                                            get_gen_id_0 = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner.Channel_gen_id_0();
                                        }
                                        return get_gen_id_0;
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name_ASCIIZ get_channel_name_ASCIIZ() {
                                        // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                        if (get_channel_name_ASCIIZ == null) {
                                            get_channel_name_ASCIIZ = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name_ASCIIZ();
                                        }
                                        return get_channel_name_ASCIIZ;
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name get_channel_name() {
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                        if (get_channel_name == null) {
                                            get_channel_name = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_name();
                                        }
                                        return get_channel_name;
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_type get_channel_type() {
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                        if (get_channel_type == null) {
                                            get_channel_type = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_channel_type();
                                        }
                                        return get_channel_type;
                                    }

                                    public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_mapping get_mapping() {
                                        // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                        // jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                        // jsignalml.JavaClassGen.paramClass(JavaClassGen.java:618)
                                        // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
                                        // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
                                        if (get_mapping == null) {
                                            get_mapping = new EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_mapping();
                                        }
                                        return get_mapping;
                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                     * jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1696)
                                     * jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1668)
                                     * 
                                     */
                                    public class Channel_gen_id_0
                                        extends ChannelClass
                                    {

                                        private int channelNum = channelCounter ++;

                                        public void createParams() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                            // jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1698)
                                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
                                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                            log.debug("Channel_gen_id_0.createParams()");
                                        }

                                        public void createChannels() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1667)
                                            // jsignalml.JavaClassGen.channelClass(JavaClassGen.java:1698)
                                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
                                            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
                                            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
                                            log.debug("Channel_gen_id_0.createChannels()");
                                        }

                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1670)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "gen_id_0";
                                        }

                                        protected MyBuffer _buffer() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1671)
                                            // jsignalml.JavaClassGen.underBufferMethod(JavaClassGen.java:1716)
                                            return buffer();
                                        }

                                        public TypeString getSampleFormat() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1672)
                                            // jsignalml.JavaClassGen.sampleFormatMethod(JavaClassGen.java:1728)
                                            // node.format.type=TypeString
                                            TypeString value = new TypeString("<i2");
                                            return ((TypeString) value);
                                        }

                                        public TypeInt mapSample(long sample) {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1673)
                                            // jsignalml.JavaClassGen.mapSampleMethod(JavaClassGen.java:1741)
                                            Type value = get_mapping().get();
                                            return TypeInt.I.make(value.call(new TypeInt(sample)));
                                        }

                                        public float getSample(long sample) {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1674)
                                            // jsignalml.JavaClassGen.getSampleMethod(JavaClassGen.java:1756)
                                            TypeString format_ = this.getSampleFormat();
                                            BitForm format = BitForm.get(format_);
                                            ByteBuffer buffer = _buffer().source;
                                            Type mapping = get_mapping().get();
                                            if (isBinary()) {
                                                Type input = format.read(buffer, ((TypeInt) mapping.call(new TypeInt(sample))));
                                                float rawValue = ((float) TypeFloat.I.make(input).value);
                                                return applyLinearTransformation(rawValue);
                                            } else {
                                                float rawValue = getScanner().readFloat(((TypeInt) mapping.call(new TypeInt(sample))).value.intValue());
                                                return applyLinearTransformation(rawValue);
                                            }
                                        }

                                        public void getSamples(FloatBuffer dst, long sample) {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1675)
                                            // jsignalml.JavaClassGen.getSamplesMethod(JavaClassGen.java:1833)
                                            TypeString format_ = this.getSampleFormat();
                                            BitForm format = BitForm.get(format_);
                                            ByteBuffer buffer = _buffer().source;
                                            Type mapping = get_mapping().get();
                                            if (isBinary()) {
                                                while (dst.hasRemaining()) {
                                                    Type input = format.read(buffer, ((TypeInt) mapping.call(new TypeInt(sample ++))));
                                                    float rawValue = ((float) TypeFloat.I.make(input).value);
                                                    dst.put(applyLinearTransformation(rawValue));
                                                }
                                            } else {
                                                while (dst.hasRemaining()) {
                                                    float rawValue = getScanner().readFloat(((TypeInt) mapping.call(new TypeInt(sample ++))).value.intValue());
                                                    dst.put(applyLinearTransformation(rawValue));
                                                }
                                            }
                                        }

                                        private float applyLinearTransformation(float rawValue) {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1676)
                                            // jsignalml.JavaClassGen.applyLinearTransformationMethod(JavaClassGen.java:1917)
                                            float calibGain = getCalibrationGain().getValue().floatValue();
                                            float calibOffs = getCalibrationOffset().getValue().floatValue();
                                            return ((rawValue-calibOffs)*calibGain);
                                        }

                                        public double getSamplingFrequency() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1677)
                                            // jsignalml.JavaClassGen.getSamplingFrequencyMethod(JavaClassGen.java:1947)
                                            Type value = get_sampling_frequency().get();
                                            TypeFloat cast = TypeFloat.I.make(value);
                                            return cast.getValue();
                                        }

                                        public long getNumberOfSamples() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1678)
                                            // jsignalml.JavaClassGen.getNumberOfSamplesMethod(JavaClassGen.java:1960)
                                            Type value = get_number_of_samples().get();
                                            TypeInt cast = TypeInt.I.make(value);
                                            return cast.safeLongValue();
                                        }

                                        public java.lang.String getChannelName() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1679)
                                            // jsignalml.JavaClassGen.getChannelNameMethod(JavaClassGen.java:1973)
                                            Type value = get_channel_name().get();
                                            TypeString stringValue = ((TypeString) value);
                                            java.lang.String strValue = stringValue.getValue();
                                            if (strValue.equals("")) {
                                                return ("L"+ channelNum);
                                            }
                                            return strValue;
                                        }

                                        public java.lang.String getChannelType() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1680)
                                            // jsignalml.JavaClassGen.getChannelTypeMethod(JavaClassGen.java:2009)
                                            Type value = get_channel_type().get();
                                            TypeString stringValue = ((TypeString) value);
                                            return stringValue.getValue();
                                        }

                                        public TypeFloat getCalibrationGain() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1681)
                                            // jsignalml.JavaClassGen.getCalibrationGainMethod(JavaClassGen.java:2025)
                                            Type value = get_calibration_gain().get();
                                            TypeFloat cast = TypeFloat.I.make(value);
                                            return cast;
                                        }

                                        public TypeFloat getCalibrationOffset() {
                                            // jsignalml.ASTNode$Channel._accept(ASTNode.java:188)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1682)
                                            // jsignalml.JavaClassGen.getCalibrationOffsetMethod(JavaClassGen.java:2041)
                                            Type value = get_calibration_offset().get();
                                            TypeFloat cast = TypeFloat.I.make(value);
                                            return cast;
                                        }

                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                     * jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1590)
                                     * 
                                     */
                                    public class index
                                        extends Param<TypeInt>
                                    {


                                        index(TypeInt index) {
                                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1594)
                                            this.cache = index;
                                        }

                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1597)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "index";
                                        }

                                        protected TypeInt _get() {
                                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1604)
                                            throw new RuntimeException();
                                        }

                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                     * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                                     * node.type=TypeString
                                     * --> nodetype=TypeString
                                     * 
                                     */
                                    public class _param_channel_name
                                        extends Param<TypeString>
                                    {


                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "channel_name";
                                        }

                                        protected TypeString _get() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                                            // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                                            // node.type=TypeString
                                            // node.expr.type=TypeString
                                            // --> nodetype=TypeString
                                            return ((TypeString) Builtins.trim().call(get_channel_name_ASCIIZ().get()));
                                        }

                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                                     * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                                     * node.type=TypeString
                                     * --> nodetype=TypeString
                                     * 
                                     */
                                    public class _param_channel_name_ASCIIZ
                                        extends Param<TypeString>
                                    {


                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "channel_name_ASCIIZ";
                                        }

                                        protected TypeString _get() {
                                            // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                                            // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                                            // node.type=TypeString
                                            // node._read_type=TypeString
                                            // --> nodetype=TypeString
                                            // format=("|S4")
                                            // format.type=TypeString
                                            // offset=(offset + 4 + index * 4)
                                            // offset.type=unknown
                                            TypeInt offset = ((TypeInt) get_offset().get().add(new TypeInt(4)).add(get_index().get().mul(new TypeInt(4))));
                                            BitForm.String theformat = (new jsignalml.BitForm.String(4));
                                            TypeString input = theformat.read(buffer().source, offset);
                                            return ((TypeString) input);
                                        }

                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                     * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                                     * node.type=TypeString
                                     * --> nodetype=TypeString
                                     * 
                                     */
                                    public class _param_channel_type
                                        extends Param<TypeString>
                                    {


                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "channel_type";
                                        }

                                        protected TypeString _get() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                                            // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                                            // node.type=TypeString
                                            // node.expr.type=TypeString
                                            // --> nodetype=TypeString
                                            return ((TypeString) new TypeString("EEG"));
                                        }

                                    }


                                    /**
                                     * 
                                     * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                                     * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                                     * node.type=TypeInt
                                     * --> nodetype=TypeInt
                                     * 
                                     */
                                    public class _param_mapping
                                        extends FunctionParam<TypeInt>
                                    {


                                        public java.lang.String id() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                            return "mapping";
                                        }

                                        public EASYS.File_main.ChannelSet_data.Loop_extended_header.extended_header_inner.If_if_ext_hdr.If_if_record_channel_names.Loop_channels.channels_inner._param_mapping get() {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:554)
                                            // jsignalml.JavaClassGen.getThisMethod(JavaClassGen.java:1014)
                                            return this;
                                        }

                                        public TypeInt call(TypeInt sample) {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:555)
                                            // jsignalml.JavaClassGen.callExprMethod(JavaClassGen.java:1047)
                                            // node.type=TypeInt
                                            // node.expr=(data_offset + (sample * number_of_channels + index) * data_cell_size)
                                            // node.expr.type=TypeInt
                                            // --> nodetype=TypeInt
                                            return ((TypeInt) get_data_offset().get().add(sample.mul(get_number_of_channels().get()).add(get_index().get()).mul(get_data_cell_size().get())));
                                        }

                                        public TypeInt call(List<Type> args) {
                                            // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:555)
                                            // jsignalml.JavaClassGen.callExprMethod(JavaClassGen.java:1057)
                                            if (args.size()!= 1) {
                                                throw new jsignalml.ExpressionFault.ArgMismatch(args.size(), 1);
                                            }
                                            return this.call(((TypeInt) args.get(0)));
                                        }

                                    }

                                }

                            }

                        }


                        /**
                         * 
                         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
                         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                         * node.type=TypeInt
                         * --> nodetype=unknown
                         * 
                         */
                        public class _param_offset
                            extends Param<Type>
                        {


                            public java.lang.String id() {
                                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                return "offset";
                            }

                            protected Type _get() {
                                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                                // node.type=TypeInt
                                // node.expr.type=unknown
                                // --> nodetype=unknown
                                return (((get_indexHdr().get().compareTo(new TypeInt(0)) == 0)?TypeInt.True:TypeInt.False).isTrue()?get_extended_header_offset().get():get_extended_header().get().index(get_indexHdr().get().sub(new TypeInt(1))).access("if_ext_hdr").access("offset").add(get_extended_header().get().index(get_indexHdr().get().sub(new TypeInt(1))).access("if_ext_hdr").access("record_size")).add(new TypeInt(4)));
                            }

                        }


                        /**
                         * 
                         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                         * node.type=TypeString
                         * --> nodetype=TypeString
                         * 
                         */
                        public class _param_record_mnemonic
                            extends Param<TypeString>
                        {


                            public java.lang.String id() {
                                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                return "record_mnemonic";
                            }

                            protected TypeString _get() {
                                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                                // node.type=TypeString
                                // node._read_type=TypeString
                                // --> nodetype=TypeString
                                // format=("|S2")
                                // format.type=TypeString
                                // offset=(offset)
                                // offset.type=unknown
                                TypeInt offset = ((TypeInt) get_offset().get());
                                BitForm.String theformat = (new jsignalml.BitForm.String(2));
                                TypeString input = theformat.read(buffer().source, offset);
                                return ((TypeString) input);
                            }

                        }


                        /**
                         * 
                         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
                         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
                         * node.type=TypeInt
                         * --> nodetype=TypeInt
                         * 
                         */
                        public class _param_record_size
                            extends Param<TypeInt>
                        {


                            public java.lang.String id() {
                                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                                return "record_size";
                            }

                            protected TypeInt _get() {
                                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                                // node.type=TypeInt
                                // node._read_type=TypeInt
                                // --> nodetype=TypeInt
                                // format=("<u2")
                                // format.type=TypeString
                                // offset=(offset + 2)
                                // offset.type=unknown
                                TypeInt offset = ((TypeInt) get_offset().get().add(new TypeInt(2)));
                                BitForm.Int.Unsigned16 .LE theformat = (new jsignalml.BitForm.Int.Unsigned16.LE());
                                TypeInt input = theformat.read(buffer().source, offset);
                                return ((TypeInt) input);
                            }

                        }

                    }


                    /**
                     * 
                     * jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                     * jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1590)
                     * 
                     */
                    public class indexHdr
                        extends Param<TypeInt>
                    {


                        indexHdr(TypeInt index) {
                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1594)
                            this.cache = index;
                        }

                        public java.lang.String id() {
                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1597)
                            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                            return "indexHdr";
                        }

                        protected TypeInt _get() {
                            // jsignalml.ASTNode$Itername._accept(ASTNode.java:520)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1314)
                            // jsignalml.JavaClassGen.indexClass(JavaClassGen.java:1604)
                            throw new RuntimeException();
                        }

                    }

                }

            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=TypeFloat
         * --> nodetype=TypeFloat
         * 
         */
        public class _param_calibration_gain
            extends Param<TypeFloat>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "calibration_gain";
            }

            protected TypeFloat _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=TypeFloat
                // node.expr.type=TypeFloat
                // --> nodetype=TypeFloat
                return ((TypeFloat) new TypeInt(1).div(get__calibration_unit().get()));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_calibration_offset
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "calibration_offset";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("<i2")
                // format.type=TypeString
                // offset=(26)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(26));
                BitForm.Int.Int16 .LE theformat = (new jsignalml.BitForm.Int.Int16.LE());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_data_cell_size
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "data_cell_size";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=unknown
                // node.expr.type=TypeInt
                // --> nodetype=TypeInt
                return ((TypeInt) new TypeInt(2).pow(get_data_validation_field().get().bin_and(new TypeInt(3)).sub(new TypeInt(1))));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_data_offset
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "data_offset";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=unknown
                // node.expr.type=TypeInt
                // --> nodetype=TypeInt
                return ((TypeInt) get_data_org().get().mul(new TypeInt(16)));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_data_org
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "data_org";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("<u2")
                // format.type=TypeString
                // offset=(28)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(28));
                BitForm.Int.Unsigned16 .LE theformat = (new jsignalml.BitForm.Int.Unsigned16.LE());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_data_validation_field
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "data_validation_field";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("|i1")
                // format.type=TypeString
                // offset=(24)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(24));
                BitForm.Int.Int8 theformat = (new jsignalml.BitForm.Int.Int8());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_extended_header_offset
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "extended_header_offset";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=unknown
                // node.expr.type=TypeInt
                // --> nodetype=TypeInt
                return ((TypeInt) get_xhdr_org().get().mul(new TypeInt(16)));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeString
         * 
         */
        public class _param_file_type
            extends Param<TypeString>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "file_type";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeString
                // --> nodetype=TypeString
                // format=("|S1")
                // format.type=TypeString
                // offset=(15)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(15));
                BitForm.String theformat = (new jsignalml.BitForm.String(1));
                TypeString input = theformat.read(buffer().source, offset);
                return ((TypeString) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeString
         * 
         */
        public class _param_magic
            extends Param<TypeString>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "magic";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=unknown
                // node.expr.type=TypeString
                // --> nodetype=TypeString
                return ((TypeString) get_signature().get().slice(null, new TypeInt(3), null));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=TypeInt
         * --> nodetype=TypeInt
         * 
         */
        public class _param_mapping
            extends FunctionParam<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "mapping";
            }

            public EASYS.File_main._param_mapping get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:554)
                // jsignalml.JavaClassGen.getThisMethod(JavaClassGen.java:1014)
                return this;
            }

            public TypeInt call(TypeInt sample_number, TypeInt channel_number) {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:555)
                // jsignalml.JavaClassGen.callExprMethod(JavaClassGen.java:1047)
                // node.type=TypeInt
                // node.expr=((number_of_channels * sample_number + channel_number) * data_cell_size + data_offset)
                // node.expr.type=TypeInt
                // --> nodetype=TypeInt
                return ((TypeInt) get_number_of_channels().get().mul(sample_number).add(channel_number).mul(get_data_cell_size().get()).add(get_data_offset().get()));
            }

            public TypeInt call(List<Type> args) {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:555)
                // jsignalml.JavaClassGen.callExprMethod(JavaClassGen.java:1057)
                if (args.size()!= 2) {
                    throw new jsignalml.ExpressionFault.ArgMismatch(args.size(), 2);
                }
                return this.call(((TypeInt) args.get(0)), ((TypeInt) args.get(1)));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_number_of_auxiliary_channels
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "number_of_auxiliary_channels";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("|u1")
                // format.type=TypeString
                // offset=(17)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(17));
                BitForm.Int.Unsigned8 theformat = (new jsignalml.BitForm.Int.Unsigned8());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_number_of_channels
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "number_of_channels";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("|u1")
                // format.type=TypeString
                // offset=(16)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(16));
                BitForm.Int.Unsigned8 theformat = (new jsignalml.BitForm.Int.Unsigned8());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param_number_of_samples
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "number_of_samples";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("<i4")
                // format.type=TypeString
                // offset=(20)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(20));
                BitForm.Int.Int32 .LE theformat = (new jsignalml.BitForm.Int.Int32.LE());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeMap
         * 
         */
        public class _param_record_name_map
            extends Param<TypeMap>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "record_name_map";
            }

            protected TypeMap _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=unknown
                // node.expr.type=TypeMap
                // --> nodetype=TypeMap
                return ((TypeMap) new TypeMap(new TypeString("AU"), new TypeString("Authentication_key"), new TypeString("FS"), new TypeString("Frequency_of_sampling"), new TypeString("ID"), new TypeString("Patient_ID_number_or_PIC"), new TypeString("RB"), new TypeString("RBLock_structure_definition"), new TypeString("TE"), new TypeString("Text_record"), new TypeString("TI"), new TypeString("Time_info"), new TypeString("TT"), new TypeString("Tag_table"), new TypeString("TX"), new TypeString("Text_record_extension")));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=TypeFloat
         * --> nodetype=TypeFloat
         * 
         */
        public class _param_sampling_frequency
            extends Param<TypeFloat>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "sampling_frequency";
            }

            protected TypeFloat _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=TypeFloat
                // node._read_type=TypeInt
                // --> nodetype=TypeFloat
                // format=("<u2")
                // format.type=TypeString
                // offset=(18)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(18));
                BitForm.Int.Unsigned16 .LE theformat = (new jsignalml.BitForm.Int.Unsigned16.LE());
                TypeInt input = theformat.read(buffer().source, offset);
                return TypeFloat.I.make(input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeString
         * 
         */
        public class _param_signature
            extends Param<TypeString>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "signature";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeString
                // --> nodetype=TypeString
                // format=("|S15")
                // format.type=TypeString
                // offset=(0)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(0));
                BitForm.String theformat = (new jsignalml.BitForm.String(15));
                TypeString input = theformat.read(buffer().source, offset);
                return ((TypeString) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:549)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=TypeString
         * --> nodetype=TypeString
         * 
         */
        public class _param_unit
            extends Param<TypeString>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:550)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "unit";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$ExprParam._accept(ASTNode.java:340)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:552)
                // jsignalml.JavaClassGen.getExprMethod(JavaClassGen.java:992)
                // node.type=TypeString
                // node.expr.type=TypeString
                // --> nodetype=TypeString
                return ((TypeString) new TypeString("\u03bcV"));
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=TypeInt
         * --> nodetype=TypeInt
         * 
         */
        public class _param_xhdr_org
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "xhdr_org";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=TypeInt
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("<u2")
                // format.type=TypeString
                // offset=(30)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(30));
                BitForm.Int.Unsigned16 .LE theformat = (new jsignalml.BitForm.Int.Unsigned16.LE());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }


        /**
         * 
         * jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:568)
         * jsignalml.JavaClassGen.paramClass(JavaClassGen.java:614)
         * node.type=unknown
         * --> nodetype=TypeInt
         * 
         */
        public class _param__calibration_unit
            extends Param<TypeInt>
        {


            public java.lang.String id() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:569)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "_calibration_unit";
            }

            protected TypeInt _get() {
                // jsignalml.ASTNode$BinaryParam._accept(ASTNode.java:279)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:570)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:761)
                // node.type=unknown
                // node._read_type=TypeInt
                // --> nodetype=TypeInt
                // format=("|u1")
                // format.type=TypeString
                // offset=(25)
                // offset.type=TypeInt
                TypeInt offset = ((TypeInt) new TypeInt(25));
                BitForm.Int.Unsigned8 theformat = (new jsignalml.BitForm.Int.Unsigned8());
                TypeInt input = theformat.read(buffer().source, offset);
                return ((TypeInt) input);
            }

        }

    }


    /**
     * 
     * jsignalml.ASTNode$Header._accept(ASTNode.java:690)
     * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
     * jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
     * jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1136)
     * parent paramClass=_param_format_id
     * parent paramClass=_param_codec_id
     * 
     */
    public class header
        extends Header
    {

        EASYS.header._param_format_id get_format_id = null;
        EASYS.header._param_codec_id get_codec_id = null;

        public void createParams() {
            // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
            // jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1138)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:225)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            log.debug("header.createParams()");
            // jsignalml.ASTNode$FormatID._accept(ASTNode.java:724)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:632)
            // jsignalml.JavaClassGen.formatIdClass(JavaClassGen.java:658)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("format_id", get_format_id());
            // jsignalml.ASTNode$CodecID._accept(ASTNode.java:756)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:669)
            // jsignalml.JavaClassGen.codecIdClass(JavaClassGen.java:695)
            // jsignalml.JavaClassGen$Metadata.registerParam(JavaClassGen.java:255)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            register("codec_id", get_codec_id());
        }

        public void createChannels() {
            // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1121)
            // jsignalml.JavaClassGen.headerClass(JavaClassGen.java:1138)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:248)
            // jsignalml.JavaClassGen$Metadata.<init>(JavaClassGen.java:237)
            // jsignalml.JavaClassGen.access$000(JavaClassGen.java:39)
            log.debug("header.createChannels()");
        }

        public java.lang.String id() {
            // jsignalml.ASTNode$Header._accept(ASTNode.java:690)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:1122)
            // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
            return "header";
        }

        public EASYS.header._param_format_id get_format_id() {
            // jsignalml.ASTNode$FormatID._accept(ASTNode.java:724)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:632)
            // jsignalml.JavaClassGen.formatIdClass(JavaClassGen.java:655)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_format_id == null) {
                get_format_id = new EASYS.header._param_format_id();
            }
            return get_format_id;
        }

        public EASYS.header._param_codec_id get_codec_id() {
            // jsignalml.ASTNode$CodecID._accept(ASTNode.java:756)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
            // jsignalml.JavaClassGen.visit(JavaClassGen.java:669)
            // jsignalml.JavaClassGen.codecIdClass(JavaClassGen.java:692)
            // jsignalml.JavaClassGen.classCacheMethod(JavaClassGen.java:1290)
            // jsignalml.JavaClassGen._cacheMethod(JavaClassGen.java:1300)
            if (get_codec_id == null) {
                get_codec_id = new EASYS.header._param_codec_id();
            }
            return get_codec_id;
        }


        /**
         * 
         * jsignalml.ASTNode$CodecID._accept(ASTNode.java:756)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:669)
         * jsignalml.JavaClassGen.codecIdClass(JavaClassGen.java:688)
         * node.type=TypeString
         * --> nodetype=unknown
         * 
         */
        public class _param_codec_id
            extends CodecId
        {

            public Type provider = new TypeString("Ericpol");
            public Type version = new TypeString("1.0.0");

            public java.lang.String id() {
                // jsignalml.ASTNode$CodecID._accept(ASTNode.java:756)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:670)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "codec_id";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$CodecID._accept(ASTNode.java:756)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:671)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:920)
                // node.type=TypeString
                // --> nodetype=TypeString
                // provider=("Ericpol")
                // provider.type=TypeString
                // version=("1.0.0")
                // version.type=TypeString
                TypeString value = new TypeString(((provider.toString()+":")+ version));
                return value;
            }

        }


        /**
         * 
         * jsignalml.ASTNode$FormatID._accept(ASTNode.java:724)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
         * jsignalml.JavaClassGen.visit(JavaClassGen.java:632)
         * jsignalml.JavaClassGen.formatIdClass(JavaClassGen.java:651)
         * node.type=TypeString
         * --> nodetype=unknown
         * 
         */
        public class _param_format_id
            extends FormatId
        {

            public Type name = new TypeString("PE-EASYS");
            public Type provider = new TypeString("-");
            public Type version = new TypeString("1.0.0");

            public java.lang.String id() {
                // jsignalml.ASTNode$FormatID._accept(ASTNode.java:724)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:633)
                // jsignalml.JavaClassGen.idMethod(JavaClassGen.java:724)
                return "format_id";
            }

            protected TypeString _get() {
                // jsignalml.ASTNode$FormatID._accept(ASTNode.java:724)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:39)
                // jsignalml.JavaClassGen.visit(JavaClassGen.java:634)
                // jsignalml.JavaClassGen.readParamFunction(JavaClassGen.java:888)
                // node.type=TypeString
                // --> nodetype=TypeString
                // name=("PE-EASYS")
                // name.type=TypeString
                // provider=("-")
                // provider.type=TypeString
                // version=("1.0.0")
                // version.type=TypeString
                TypeString value = new TypeString(((((name.toString()+":")+ provider)+":")+ version));
                return value;
            }

        }

    }

}
