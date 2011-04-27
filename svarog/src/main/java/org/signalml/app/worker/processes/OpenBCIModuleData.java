package org.signalml.app.worker.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an OpenBCI module data used to start processes.
 * The parameters are now splitted to a list. Replacing the address
 * is possible.
 *
 * @author Tomasz Sawicki
 */
public class OpenBCIModuleData {

        public static final String ADDRESS_REPLACEMENT_STRING = ">>address<<";
        /**
         * Module name.
         */
        private String name;
        /**
         * Module path.
         */
        private String path;
        /**
         * Module delay in miliseconds.
         */
        private Integer delay;
        /**
         * Module parameters.
         */
        private List<String> parameters;

        /**
         * Default constructor - this object is constructed from
         * an {@link OpenBCIModule} object.
         * 
         * @param module an {@link OpenBCIModule} object
         */
        public OpenBCIModuleData(OpenBCIModule module) {

                name = module.getName();
                path = module.getPath();
                delay = module.getDelay();
                parameters = split(module.getParameters());
        }

        /**
         * Splits the input into command line parameters.
         *
         * @param input the input
         * @return command line paramteters list
         */
        private List<String> split(String input) {

                List<String> tempList = new ArrayList<String>();
                List<String> retval = new ArrayList<String>();
                String temp = "";
                boolean singleQuotation = false;
                boolean doubleQuotation = false;

                for (int i = 0; i < input.length(); i++) {
                        if (input.charAt(i) == 92) {

                                if (i < input.length() - 1) {
                                        temp += input.charAt(++i);
                                }
                                continue;
                        }

                        if (input.charAt(i) == 32 && !singleQuotation && !doubleQuotation) {
                                tempList.add(temp);
                                temp = "";
                                continue;
                        }

                        if (!singleQuotation && input.charAt(i) == 34) {
                                doubleQuotation = !doubleQuotation;
                                continue;
                        }
                        if (!doubleQuotation && input.charAt(i) == 39) {
                                singleQuotation = !singleQuotation;
                                continue;
                        }

                        temp += input.charAt(i);
                }

                if (!temp.equals("")) {
                        tempList.add(temp);
                }

                for (int i = 0; i < tempList.size(); i++) {
                        if (!tempList.get(i).equals("")) {
                                retval.add(tempList.get(i));
                        }
                }

                return retval;
        }

        /**
         * Replaces all occurences of {@link #ADDRESS_REPLACEMENT_STRING} in parameters with given string.
         *
         * @param address given address
         */
        public void replaceAddress(String address) {

                String[] tempParameters = parameters.toArray(new String[0]);
                parameters = new ArrayList<String>();

                for (int i = 0; i < tempParameters.length; i++) {
                        Pattern pattern = Pattern.compile(ADDRESS_REPLACEMENT_STRING);
                        Matcher matcher = pattern.matcher(tempParameters[i]);
                        tempParameters[i] = matcher.replaceAll(address);
                        parameters.add(tempParameters[i]);
                }
        }

        public Integer getDelay() {
                return delay;
        }

        public String getName() {
                return name;
        }

        public List<String> getParameters() {
                return parameters;
        }

        public String getPath() {
                return path;
        }
}
