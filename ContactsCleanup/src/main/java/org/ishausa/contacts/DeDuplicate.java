package org.ishausa.contacts;

import java.io.*;
import java.util.*;

/**
 * Given an input file that is in csv format with the following header:
 * "Contact ID,First Name,Last Name,Primary Street,Primary City,Primary State/Province,Primary Zip/Postal Code,Primary Country,Is Meditator?,NCOA Address Change,NCOA Comment",
 * groups them by the address component and prints the groups out.
 */
public class DeDuplicate {
  private static final Map<String, List<Contact>> contactsGroupedByAddress = new HashMap<>();

  public static void main(String[] args) throws Exception {
    // read each line
    final List<String> lines = readLines(args[0]);
    for (final String line : lines) {
      // parse a line into Contact entity
      final Contact contact = Contact.fromCsv(line);
      // group Contacts by address (Primary Street, Primary City, Primary State/Province and Primary Zip/Postal Code)
      final String address = contact.getAddress();
      if (!contactsGroupedByAddress.containsKey(address)) {
        contactsGroupedByAddress.put(address, new ArrayList<Contact>());
      }
      contactsGroupedByAddress.get(address).add(contact);
    }
    // print out all the groups.
    printHeader();
    for (final Map.Entry<String, List<Contact>> group : contactsGroupedByAddress.entrySet()) {
      printGroup(group.getValue());
    }
  }

  private static List<String> readLines(final String fileName) throws Exception {
    final List<String> lines = new LinkedList<String>();
    final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

    String line = null;
    while ((line = br.readLine()) != null) {
      lines.add(line);
    }
    br.close();

    return lines;
  }

  private static void printHeader() {
    System.out.println("Is Primary?,Primary Contact Id,Primary Street,Primary City,Primary State,Primary Country,Group Contact Id(s),Group First Name(s),Group Last Name(s)");
  }

  private static void printGroup(final List<Contact> contacts) {
    final StringBuilder contactIds = new StringBuilder();
    final StringBuilder firstNames = new StringBuilder();
    final StringBuilder lastNames = new StringBuilder();
    for (final Contact contact : contacts) {
      contactIds.append(contact.contactId).append(", ");
      firstNames.append(contact.firstName).append(", ");
      lastNames.append(contact.lastName).append(", ");
    }
    final Contact primary = contacts.get(0);
    primary.groupContactIds = CsvParser.escapeAsCsvToken(contactIds.toString());
    primary.groupFirstNames = CsvParser.escapeAsCsvToken(firstNames.toString());
    primary.groupLastNames = CsvParser.escapeAsCsvToken(lastNames.toString());

    boolean isPrimary = true;
    for (final Contact contact : contacts) {
      printContact(contact, isPrimary);
      if (isPrimary) {
        isPrimary = false;
      }
    }
  }

  private static void printContact(final Contact contact, final boolean isPrimary) {
    final StringBuilder out = new StringBuilder();

    out.append(isPrimary ? "Y" : "N").append(",");
    out.append(contact.contactId).append(",");
    out.append(CsvParser.escapeAsCsvToken(contact.street)).append(",");
    out.append(contact.city).append(",");
    out.append(contact.state).append(",");
    out.append(contact.country).append(",");
    out.append(contact.groupContactIds).append(",");
    out.append(contact.groupFirstNames).append(",");
    out.append(contact.groupLastNames).append(",");

    System.out.println(out.toString());
  }
}

class Contact {
  private static final Map<String, Integer> FIELD_TO_CSV_INDEX_MAP = new HashMap<String, Integer>() {
    {
      put("contactId", 0);
      put("firstName", 1);
      put("lastName", 2);
      put("street", 3);
      put("city", 4);
      put("state", 5);
      put("country", 6);
      put("zip", 7);
    }
  };

  String contactId;
  String firstName;
  String lastName;
  String street;
  String city;
  String state;
  String zip;
  String country;
  boolean isMeditator;
  boolean isNcoaAddress;
  String groupContactIds = "";
  String groupFirstNames = "";
  String groupLastNames = "";

  static Contact fromCsv(String line) {
    final List<String> tokens = CsvParser.tokenize(line);
    Contact c = new Contact();
    c.contactId = getFieldIfAvailable(tokens, "contactId");
    c.firstName = getFieldIfAvailable(tokens, "firstName");
    c.lastName = getFieldIfAvailable(tokens, "lastName");
    c.street = getFieldIfAvailable(tokens, "street");
    c.city = getFieldIfAvailable(tokens, "city");
    c.state = getFieldIfAvailable(tokens, "state");
    c.zip = getFieldIfAvailable(tokens, "zip");
    c.country = getFieldIfAvailable(tokens, "country");
    c.isMeditator = "1".equals(getFieldIfAvailable(tokens, "isMeditator"));
    c.isNcoaAddress = "1".equals(getFieldIfAvailable(tokens, "isNcoaAddress"));

    return c;
  }

  private static String getFieldIfAvailable(final List<String> tokens, final String fieldName) {
    if (FIELD_TO_CSV_INDEX_MAP.containsKey(fieldName)) {
      if (tokens.size() > FIELD_TO_CSV_INDEX_MAP.get(fieldName)) {
        return tokens.get(FIELD_TO_CSV_INDEX_MAP.get(fieldName));
      }
    }
    return "";
  }

  String getAddress() {
    if (street == null || street.trim().isEmpty()) {
      // If street address is empty, treat it as an unique address
      return String.valueOf(System.currentTimeMillis());
    }
    return street + city + state + zip;
  }
}

class CsvParser {
  private static enum ParseState {
    NORMAL,
    IN_QUOTES,
  }

  /**
   * Assumes given line is in proper csv format - which comprises of the following rules:
   * 1. tokens are delimited by ',', for eg., 0034XYZ,23 Random Ave. N,Bellevue,WA,US
   * 2. token that has a ',' as part of its value is surrounded with a ", for eg., "234, Somewhere St."
   * 3. token that has a '"' as part of its value is surrounded with a " and each occurrence is escaped with another ", for eg., "John ""Middle"" Smith"
   */
  public static List<String> tokenize(final String csvLine) {
    final List<String> tokens = new ArrayList<>();
    final char[] chars = csvLine.toCharArray();

    final StringBuilder tokenBuilder = new StringBuilder();
    ParseState state = ParseState.NORMAL;

    for (int i = 0; i < chars.length; ++i) {
      final char cur = chars[i];
      final char lookAhead = (i + 1 < chars.length) ? chars[i + 1] : '\0';
      boolean shouldAppendToOutput = true;
      boolean shouldIgnoreNext = false;
      switch (cur) {
        case '"':
          if (lookAhead == '"') {
            shouldIgnoreNext = true;
          } else {
            shouldAppendToOutput = false;
            state = (state == ParseState.NORMAL) ? ParseState.IN_QUOTES : ParseState.NORMAL;
          }
          break;
        case ',':
          if (state != ParseState.IN_QUOTES) {
            shouldAppendToOutput = false;
            tokens.add(tokenBuilder.toString());
            tokenBuilder.setLength(0);  //clear the builder
          }
          break;
      }
      if (shouldAppendToOutput) {
        tokenBuilder.append(cur);
      }
      if (shouldIgnoreNext) {
        ++i;
      }
    }

    tokens.add(tokenBuilder.toString());

    return tokens;
  }

  public static String escapeAsCsvToken(final String value) {
    final boolean shouldSurroundWithQuotes = (value.indexOf('"') > 0 || value.indexOf(',') > 0);
    final String escapedValue = value.replaceAll("\"", "\"\"");
    return shouldSurroundWithQuotes ? '"' + escapedValue + '"' : escapedValue;
  }
}
