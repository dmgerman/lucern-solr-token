begin_unit
begin_package
DECL|package|org.apache.solr.solrjs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|solrjs
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CommonsHttpSolrServer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_class
DECL|class|ReutersService
specifier|public
class|class
name|ReutersService
block|{
DECL|field|countryCodesMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|countryCodesMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|SolrServerException
throws|,
name|ParseException
block|{
name|String
name|usage
init|=
literal|"Usage: java -jar reutersimporter.jar<solrUrl><datadir>"
decl_stmt|;
name|URL
name|solrUrl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|solrUrl
operator|=
operator|new
name|URL
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"First argument needs to be an URL!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
name|File
name|baseDir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|baseDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|baseDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Second argument needs to be an existing directory!"
argument_list|)
expr_stmt|;
empty_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Second argument needs to be an existing directory!"
argument_list|)
expr_stmt|;
empty_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|solrUrl
operator|!=
literal|null
operator|&&
name|baseDir
operator|!=
literal|null
operator|&&
name|baseDir
operator|.
name|exists
argument_list|()
operator|&&
name|baseDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|ReutersService
name|reutersService
init|=
operator|new
name|ReutersService
argument_list|(
name|solrUrl
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|reutersService
operator|.
name|readDirectory
argument_list|(
name|baseDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * The Solr Server to use 	 */
DECL|field|solrServer
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
comment|/** 	 * A shared xpath instance 	 */
DECL|field|xPath
specifier|private
specifier|final
name|XPath
name|xPath
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXPath
argument_list|()
decl_stmt|;
comment|/** 	 * The format used in the sgml files. 	 * eg. 26-FEB-1987 15:01:01.79 	 */
DECL|field|reutersDateFormat
specifier|private
specifier|final
name|SimpleDateFormat
name|reutersDateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd-MMM-yyyy kk:mm:ss.SS"
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
comment|/** 	 * A service that inputs the reuters TODO dataset. 	 * @param solrUrl The url of the solr server. 	 */
DECL|method|ReutersService
specifier|public
name|ReutersService
parameter_list|(
name|String
name|solrUrl
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|solrServer
operator|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|solrUrl
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrServer
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to connect to solr server: "
operator|+
name|solrUrl
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Takes a<REUTERS> node and converts it into a SolrInoutDocument. 	 * @param element A<REUTERS> node. 	 * @throws XPathExpressionException 	 * @throws SolrServerException 	 * @throws IOException 	 * @throws ParseException  	 */
DECL|method|readDocument
specifier|public
name|void
name|readDocument
parameter_list|(
name|Element
name|element
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|SolrServerException
throws|,
name|IOException
throws|,
name|ParseException
block|{
name|SolrInputDocument
name|inputDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|element
operator|.
name|getAttribute
argument_list|(
literal|"NEWID"
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"TEXT/TITLE"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"dateline"
argument_list|,
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"TEXT/DATELINE"
argument_list|,
name|element
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"TEXT/BODY"
argument_list|,
name|element
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"places"
argument_list|,
name|readList
argument_list|(
literal|"PLACES/D"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"topics"
argument_list|,
name|readList
argument_list|(
literal|"TOPICS/D"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"organisations"
argument_list|,
name|readList
argument_list|(
literal|"ORGS/D"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"exchanges"
argument_list|,
name|readList
argument_list|(
literal|"EXCHANGES/D"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"companies"
argument_list|,
name|readList
argument_list|(
literal|"COMPANIES/D"
argument_list|,
name|element
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"date"
argument_list|,
name|this
operator|.
name|reutersDateFormat
operator|.
name|parse
argument_list|(
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"DATE"
argument_list|,
name|element
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"date"
argument_list|,
name|this
operator|.
name|reutersDateFormat
operator|.
name|parse
argument_list|(
literal|"0"
operator|+
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"DATE"
argument_list|,
name|element
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|place
range|:
name|inputDocument
operator|.
name|getFieldValues
argument_list|(
literal|"places"
argument_list|)
control|)
block|{
name|String
name|code
init|=
name|this
operator|.
name|countryCodesMap
operator|.
name|get
argument_list|(
name|place
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|code
operator|=
name|getCodeForPlace
argument_list|(
operator|(
name|String
operator|)
name|place
argument_list|)
expr_stmt|;
name|this
operator|.
name|countryCodesMap
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|place
argument_list|,
name|code
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"countryCodes"
argument_list|,
name|code
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|solrServer
operator|.
name|add
argument_list|(
name|inputDocument
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|inputDocument
operator|.
name|getField
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCodeForPlace
specifier|private
name|String
name|getCodeForPlace
parameter_list|(
name|String
name|place
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|XPathExpressionException
block|{
name|DocumentBuilderFactory
name|dbfac
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
name|dbfac
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|docBuilder
operator|.
name|parse
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http://ws.geonames.org/search?q="
operator|+
name|place
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"/geonames/geoname/countryCode"
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|/** 	 * Reads a whole .sgml file. 	 * @param file The sgml reuters file. 	 * @throws XPathExpressionException 	 * @throws IOException 	 * @throws ParserConfigurationException 	 * @throws SAXException 	 * @throws SolrServerException 	 * @throws ParseException  	 */
DECL|method|readFile
specifier|public
name|void
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|SolrServerException
throws|,
name|ParseException
block|{
name|String
name|documentString
init|=
name|readFileAsString
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// remove "bad" entities
name|documentString
operator|=
name|documentString
operator|.
name|replaceAll
argument_list|(
literal|"&#\\d;"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|documentString
operator|=
name|documentString
operator|.
name|replaceAll
argument_list|(
literal|"&#\\d\\d;"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// remove doctype declaration
name|documentString
operator|=
name|documentString
operator|.
name|replaceAll
argument_list|(
literal|"<!DOCTYPE lewis SYSTEM \"lewis.dtd\">"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// add a document root
name|documentString
operator|=
literal|"<root>"
operator|+
name|documentString
operator|+
literal|"</root>"
expr_stmt|;
name|DocumentBuilderFactory
name|dbfac
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
name|dbfac
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|docBuilder
operator|.
name|parse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|documentString
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|NodeList
name|nodeList
init|=
operator|(
name|NodeList
operator|)
name|xPath
operator|.
name|evaluate
argument_list|(
literal|"/root/REUTERS"
argument_list|,
name|doc
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"READING FILE: "
operator|+
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" - "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|i
operator|+
literal|") "
argument_list|)
expr_stmt|;
name|readDocument
argument_list|(
operator|(
name|Element
operator|)
name|nodeList
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Reads a whole directory containing reuters .sgml files- 	 * @param directory 	 * @throws XPathExpressionException 	 * @throws IOException 	 * @throws ParserConfigurationException 	 * @throws SAXException 	 * @throws SolrServerException 	 * @throws ParseException  	 */
DECL|method|readDirectory
specifier|public
name|void
name|readDirectory
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|XPathExpressionException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|SolrServerException
throws|,
name|ParseException
block|{
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
if|if
condition|(
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|".sgm"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Directory doesn't contain sgml files!"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|readFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Helper that converts a listnode into a java list.      * @param path      * @param element      * @return      * @throws XPathExpressionException      */
DECL|method|readList
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readList
parameter_list|(
name|String
name|path
parameter_list|,
name|Element
name|element
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|nodeList
init|=
operator|(
name|NodeList
operator|)
name|xPath
operator|.
name|evaluate
argument_list|(
name|path
argument_list|,
name|element
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|nodeList
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/** 	 * Helper that reads a file into a string. 	 * @param file 	 * @return 	 * @throws java.io.IOException 	 */
DECL|method|readFileAsString
specifier|private
specifier|static
name|String
name|readFileAsString
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|StringBuilder
name|fileData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|numRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|numRead
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|readData
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|numRead
argument_list|)
decl_stmt|;
name|fileData
operator|.
name|append
argument_list|(
name|readData
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|char
index|[
literal|1024
index|]
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fileData
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
