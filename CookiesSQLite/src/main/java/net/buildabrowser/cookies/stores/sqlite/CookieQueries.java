package net.buildabrowser.cookies.stores.sqlite;

public final class CookieQueries {
  
  private CookieQueries() {}

  // Session cookies are stored in-memory, not SQLite
  public static final String CREATE_COOKIES_TABLE_QUERY = """
    CREATE TABLE IF NOT EXISTS cookies (
      name VARCHAR(255) NOT NULL,
      host VARCHAR(255) NOT NULL,
      path VARCHAR(255) NOT NULL,
      value VARCHAR(255) NOT NULL,
      
      secure BOOLEAN NOT NULL,
      host_only BOOLEAN NOT NULL,
      http_only BOOLEAN NOT NULL,
      
      has_path_attribute BOOLEAN NOT NULL,
      same_site INTEGER NOT NULL,
      
      creation_time TIMESTAMP WITH TIME ZONE NOT NULL,
      expiry_time TIMESTAMP WITH TIME ZONE NOT NULL,
      last_access_time TIMESTAMP WITH TIME ZONE NOT NULL,

      PRIMARY KEY (name, host, host_only, path)
    );
    """;
  
  public static final String UPDATE_COOKIE_LAST_ACCESS = """
    UPDATE cookies
    SET last_access_time = ?
    WHERE name = ? AND host = ? AND path = ? AND host_only = ?;
  """;

  public static final String CREATE_COOKIE_QUERY = """
    INSERT INTO cookies (name, host, path, value, secure, host_only, http_only, has_path_attribute, same_site, creation_time, expiry_time, last_access_time)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT(name, host, host_only, path) DO UPDATE SET
      value = excluded.value,
      secure = excluded.secure,
      http_only = excluded.http_only,
      has_path_attribute = excluded.has_path_attribute,
      same_site = excluded.same_site,
      expiry_time = excluded.expiry_time,
      last_access_time = excluded.last_access_time;
    """;

  // TODO: Make the query more robust
  public static final String RETRIEVE_COOKIES_QUERY
    = "SELECT * FROM cookies WHERE host LIKE '%' || ?;";

  // TODO: Use proper host-equals
  public static final String RETRIEVE_DUPLICATE_COOKIE_QUERY
    = "SELECT * FROM cookies WHERE name = ? AND host = ? AND path = ? AND host_only = ?;";
  
  public static final String RETRIEVE_SECURE_COOKIES_QUERY
    = "SELECT * FROM cookies WHERE ((host LIKE '%' || ?) OR (? LIKE '%' || host)) AND secure = true;";

}
