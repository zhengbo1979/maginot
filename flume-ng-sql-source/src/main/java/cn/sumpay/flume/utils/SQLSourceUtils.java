package cn.sumpay.flume.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import org.apache.flume.Context;
import org.apache.flume.conf.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLSourceUtils
{
  private static final Logger log = LoggerFactory.getLogger(SQLSourceUtils.class);
  private String statusFilePath;
  private String statusFileName;
  private String connectionURL;
  private String table;
  private String incrementalColumnName;
  private String columnsToSelect;
  private String user;
  private String password;
  private String driverName;
  private String customQuery;
  private int runQueryDelay;
  private int batchSize;
  private int maxRows;
  private long incrementalValue;
  private File file;
  private File directory;
  
  public SQLSourceUtils(Context context)
    throws ConfigurationException
  {
    this.statusFilePath = context.getString("status.file.path", "/var/lib/flume");
    this.statusFileName = context.getString("status.file.name");
    this.connectionURL = context.getString("connection.url");
    this.table = context.getString("table");
    this.incrementalColumnName = context.getString("incremental.column.name");
    this.columnsToSelect = context.getString("columns.to.select", "*");
    this.runQueryDelay = context.getInteger("run.query.delay", Integer.valueOf(10000)).intValue();
    this.user = context.getString("user");
    this.password = context.getString("password");
    this.directory = new File(getStatusFilePath());
    this.customQuery = context.getString("custom.query");
    this.batchSize = context.getInteger("batch.size", Integer.valueOf(100)).intValue();
    this.maxRows = context.getInteger("max.rows", Integer.valueOf(10000)).intValue();
    
    checkMandatoryProperties();
    setDriverNameFromURL();
    if (!isStatusDirectoryCreated()) {
      createDirectory();
    }
    this.file = new File(getStatusFilePath() + "/" + getStatusFileName());
    
    this.incrementalValue = getStatusFileIncrement(context.getLong("incremental.value", Long.valueOf(0L)).longValue());
  }
  
  public void updateStatusFile(long id)
    throws NumberFormatException, SQLException
  {
    log.info("Updating status file");
    setIncrementalValue(id);
    log.info("Last row increment value readed: " + getIncrementalValue() + ", updating status file...");

    writeStatusFile();
  }
  
  private boolean isStatusFileCreated()
  {
    return (this.file.exists()) && (!this.file.isDirectory());
  }
  
  private boolean isStatusDirectoryCreated()
  {
    return (this.directory.exists()) && (!this.directory.isFile());
  }
  
  private long getStatusFileIncrement(long configuredStartValue)
  {
    if (!isStatusFileCreated())
    {
      log.info("Status file not created, using start value from config file");
      return configuredStartValue;
    }
    try
    {
      FileReader reader = new FileReader(this.file);
      char[] chars = new char[(int)this.file.length()];
      reader.read(chars);
      String[] statusInfo = new String(chars).split(" ");
      if ((statusInfo[0].equals(this.connectionURL)) && (statusInfo[1].equals(this.table)) && (statusInfo[2].equals(this.incrementalColumnName)))
      {
        reader.close();
        log.info(this.statusFilePath + "/" + this.statusFileName + " correctly formed");
        return Long.parseLong(statusInfo[3], 10);
      }
      log.warn(this.statusFilePath + "/" + this.statusFileName + " corrupt!!! Deleting it.");
      reader.close();
      deleteStatusFile();
      return configuredStartValue;
    }
    catch (Exception e)
    {
      log.error("Corrupt increment value in file!!! Deleting it.");
      deleteStatusFile();
    }
    return configuredStartValue;
  }
  
  private void deleteStatusFile()
  {
    if (this.file.delete()) {
      log.info("Deleted status file: {}", this.file.getAbsolutePath());
    } else {
      log.warn("Error deleting file: {}", this.file.getAbsolutePath());
    }
  }
  
  private void writeStatusFile()
  {
    try
    {
      Writer writer = new FileWriter(this.file, false);
      writer.write(this.connectionURL + " ");
      writer.write(this.table + " ");
      writer.write(this.incrementalColumnName + " ");
      writer.write(Long.toString(getIncrementalValue()) + " \n");
      writer.close();
    }
    catch (IOException e)
    {
      log.error("Error writing incremental value to status file!!!");
    }
  }
  
  private void checkMandatoryProperties()
    throws ConfigurationException
  {
    if (getStatusFileName() == null) {
      throw new ConfigurationException("status.file.name property not set");
    }
    if (getConnectionURL() == null) {
      throw new ConfigurationException("connection.url property not set");
    }
    if ((getTable() == null) && (getCustomQuery() == null)) {
      throw new ConfigurationException("property table not set");
    }
    if (getIncrementalColumnName() == null) {
      throw new ConfigurationException("incremental.column.name  property not set");
    }
    if (getPasswordDatabase() == null) {
      throw new ConfigurationException("password property not set");
    }
    if (getUserDataBase() == null) {
      throw new ConfigurationException("user property not set");
    }
  }
  
  private String getStatusFilePath()
  {
    return this.statusFilePath;
  }
  
  private String getStatusFileName()
  {
    return this.statusFileName;
  }
  
  public String getConnectionURL()
  {
    return this.connectionURL;
  }
  
  public String getTable()
  {
    return this.table;
  }
  
  public String getIncrementalColumnName()
  {
    return this.incrementalColumnName;
  }
  
  private File getDirectory()
  {
    return this.directory;
  }
  
  private boolean createDirectory()
  {
    return getDirectory().mkdir();
  }
  
  public String getColumnsToSelect()
  {
    return this.columnsToSelect;
  }
  
  public String getCustomQuery()
  {
    return this.customQuery;
  }
  
  public long getIncrementalValue()
  {
    return this.incrementalValue;
  }
  
  private void setIncrementalValue(long newValue)
  {
    this.incrementalValue = newValue;
  }
  
  public String getUserDataBase()
  {
    return this.user;
  }
  
  public String getPasswordDatabase()
  {
    return this.password;
  }
  
  public int getRunQueryDelay()
  {
    return this.runQueryDelay;
  }
  
  public String getDriverName()
  {
    return this.driverName;
  }
  
  public int getBatchSize()
  {
    return this.batchSize;
  }
  
  public int getMaxRows()
  {
    return this.maxRows;
  }
  
  private void setDriverNameFromURL()
  {
    String[] stringsURL = this.connectionURL.split(":");
    if (stringsURL[0].equals("jdbc"))
    {
      this.driverName = stringsURL[1];
      log.info("SQL Driver name: " + this.driverName);
    }
    else
    {
      log.warn("Error: impossible to get driver name from  " + getConnectionURL());
      
      this.driverName = "unknown";
    }
  }
}