package com.dextrys.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mapper {
    private Map<String, String> _mapping = null;
    private ComparisonType _type = null;
    private String _value = null;
    private String _key = null;
    private Map<String, String> _data = null;

    private Mapper() {
        super();
    }

    public static Mapper getSummaryMapper() {
        // map.key - label on the summary area
        // map.value - corresponding question (or complex prioritized expression) on the Report a Safety Event form
        // precede value with comparison code separated with ' | '
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("Event Type", "Event Type");
        mapping.put("Department", "Department where the event occurred? | Department where the event occurred");
        mapping.put("Where the event occurred", "Department where the event occurred? | Department where the event occurred");
        mapping.put("Patient Name", "i/<Patient last name>, <Patient first name> | i/<Last name>, <First name>");
        mapping.put("Visitor Name", "i/<Patient last name>, <Patient first name> | i/<Last name>, <First name>");
        mapping.put("Employee/Staff Name", "i/<Patient last name>, <Patient first name> | i/<Last name>, <First name>");
        mapping.put("Actual / Near Miss", "What type of event was it?");
        mapping.put("Description:", "Briefly describe what happened");
        mapping.put("Neighboring Facility/SA", "s/<A neighboring facility was involved><Please specify department>");
        mapping.put("Nature of Event", "In which safety event category would you place this event? | Nature");
        mapping.put("DoB", "n/Date of birth");
        mapping.put("Gender", "u/Gender");
        mapping.put("MRN", "n/Medical record number (MRN)");
        mapping.put("FIN", "n/Financial information number (FIN)");
        mapping.put("Description:", "Briefly describe what happened");
        mapping.put("Affected Person Category", "<Who was the affected party?>-<Patient type>");
        mapping.put("Severity/Harm", "~/Please indicate the degree of harm | u/What was the severity of the event?");
        mapping.put("Occurred", "~/When did the event occur?");
        mapping.put("Reported", "~/Today");
        mapping.put("Originally Submitted by", "~/Submitter");
        mapping.put("Last update", "~/Submitter");
        mapping.put("Source", "Source");
        Mapper obj = new Mapper();
        obj._mapping = mapping;
        return obj;
    }

    public static Mapper getAuditTrailMapper() {
        // map.key - questions in the Audit Trail table
        // map.value - questions we get during filling the form
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("Harm Type" ,"TODO");
        mapping.put("Equipment Manufacturer" ,"TODO");
        mapping.put("Event Occurrence Time" ,"p/When did the event occur?");
        mapping.put("Event Occurrence Date" ,"p/When did the event occur?");
        mapping.put("Equipment Model Number" ,"TODO");
        mapping.put("Discovery Date" ,"p/When was the event discovered?");
        mapping.put("Date of Birth" ,"Date of birth");
        mapping.put("Location" ,"TODO");
        mapping.put("Inventory Number" ,"TODO");
        mapping.put("Brief Factual Description" ,"Briefly describe what happened");
        mapping.put("Nature" ,"Nature");
        mapping.put("Equipment Name" ,"TODO");
        mapping.put("Device Type" ,"TODO");
        mapping.put("Discovery Time" ,"p/When was the event discovered?");
        mapping.put("Severity" ,"TODO");
        mapping.put("Department Name" ,"Department where the event occurred?");
        mapping.put("Gender" ,"TODO");
        mapping.put("Event Reporter Name" ,"Submitter");
        mapping.put("Was a medical device involved?" ,"Was any device, equipment, or software involved?");
        mapping.put("Patient Last Name" ,"Patient last name");
        mapping.put("Sub Nature" ,"TODO");
        mapping.put("Patient Type" ,"Patient type");
        mapping.put("Event Type" ,"Event Type");
        mapping.put("Patient First Name" ,"Patient first name");
        Mapper obj = new Mapper();
        obj._mapping = mapping;
        return obj;
    }

    public static Mapper getEntryFormMapper() {
        // map.key - Working Copy
        // map.value - filled data
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("In which safety event category would you place this event?" ,"<Event Type>\n<Nature>");
        mapping.put("Department where the event occurred?" ,"~/<Department where the event occurred?> : ");
        mapping.put("Patient name" ,"<Patient last name> <Patient first name>");
        mapping.put("When did the event occur?", "~/When did the event occur?");
        mapping.put("When was the event discovered?", "~/When was the event discovered?");
        mapping.put("Procedure start time", "u/Procedure start time"); // TODO check value labels
        mapping.put("Procedure end time", "u/Procedure end time");
        mapping.put("Reporter name (Last, First)", "c/Submitter");
        Mapper obj = new Mapper();
        obj._mapping = mapping;
        return obj;
    }

    public boolean knowsAbout(String key) {
        return _mapping.containsKey(key);
    }

    public Mapper build(String key, Map<String, String> data) {
        _key = key;
        _data = data;
        _type = ComparisonType.EXACTLY;
        _value = null;
        String rule = _mapping.get(key);
        if (rule != null) {
            String[] alts = rule.split(" \\| ");
            for (String alt : alts) {
                _value = tryToBuildValue(alt, data);
                _type = getComparisonType(alt);
                if (_value != null) {
                    break;
                }
            }
        } else {
            _value = data.get(key);
        }
        return this;
    }

    public void isSuccess() {
        if (_value == null && _type != ComparisonType.NULLABLE && _type != ComparisonType.WITH_UNKNOWN) {
            throw new RuntimeException("Unable to build value for '" + _key + "' according to the mapping: " + _mapping.get(_key) + "\n\n" +
                    "Filled data: " + _data);
        }
    }

    private ComparisonType getComparisonType(String rule) {
        ComparisonType type;
        if ('/' == rule.charAt(1)) {
            type = ComparisonType.parse(rule.charAt(0));
        } else {
            type = ComparisonType.EXACTLY;
        }
        return type;
    }

    private String tryToBuildValue(String mapping, Map<String, String> data) {
        if ('/' == mapping.charAt(1)) {
            mapping = mapping.substring(2);
        }
        String result;
        if (mapping.contains("<")) {
            try {
                Pattern qp = Pattern.compile("<(.*?)>");
                Matcher matcher = qp.matcher(mapping);
                result = mapping;
                while (matcher.find()) {
                    String refValue = data.get(matcher.group(1));
                    String ref = "<" + matcher.group(1) + ">";
                    ref = ref.replaceAll("\\?", "\\\\?"); // escape question marks
                    result = result.replaceAll(ref, refValue);
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            result = data.get(mapping);
        }
        return result;
    }

    public ComparisonType getType() {
        return _type;
    }

    public String getValue() {
        return _value;
    }

    public String getRule(String key) {
        return _mapping.get(key);
    }
}

