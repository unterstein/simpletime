package neo4j.models.project;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public enum ProjectColumnType {
  HIDDEN, // just a hidden value, like ID or something
  STRING, // a string value
  BOOLEAN, // a boolean value
  TIME, // a time value
  ENUM, // an enumerated value
  ;
}
