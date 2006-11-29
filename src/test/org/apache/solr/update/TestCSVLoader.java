begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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
name|request
operator|.
name|SolrQueryRequest
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
name|core
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestCSVLoader
specifier|public
class|class
name|TestCSVLoader
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|field|filename
name|String
name|filename
init|=
literal|"solr_tmp.csv"
decl_stmt|;
DECL|field|def_charset
name|String
name|def_charset
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|file
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|deleteFile
argument_list|()
expr_stmt|;
block|}
DECL|method|makeFile
name|void
name|makeFile
parameter_list|(
name|String
name|contents
parameter_list|)
block|{
name|makeFile
argument_list|(
name|contents
argument_list|,
name|def_charset
argument_list|)
expr_stmt|;
block|}
DECL|method|makeFile
name|void
name|makeFile
parameter_list|(
name|String
name|contents
parameter_list|,
name|String
name|charset
parameter_list|)
block|{
try|try
block|{
name|Writer
name|out
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteFile
name|void
name|deleteFile
parameter_list|()
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanup
name|void
name|cleanup
parameter_list|()
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoFile
specifier|public
name|void
name|testNoFile
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteFile
argument_list|()
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
name|CSVLoader
operator|.
name|loadLocalFile
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadLocal
name|void
name|loadLocal
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|CSVLoader
operator|.
name|loadLocalFile
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|testCSVLoad
specifier|public
name|void
name|testCSVLoad
parameter_list|()
throws|throws
name|Exception
block|{
name|makeFile
argument_list|(
literal|"id\n100\n101\n102"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
comment|// csv loader currently defaults to committing
comment|// assertU(commit());
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommitFalse
specifier|public
name|void
name|testCommitFalse
parameter_list|()
throws|throws
name|Exception
block|{
name|makeFile
argument_list|(
literal|"id\n100\n101\n102"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommitTrue
specifier|public
name|void
name|testCommitTrue
parameter_list|()
throws|throws
name|Exception
block|{
name|makeFile
argument_list|(
literal|"id\n100\n101\n102"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCSV
specifier|public
name|void
name|testCSV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeFile
argument_list|(
literal|"id,str_s\n100,\"quoted\"\n101,\n102,\"\"\n103,"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
comment|// 102 is a quoted zero length field ,"", as opposed to ,,
comment|// but we can't distinguish this case (and it's debateable
comment|// if we should).  Does CSV have a way to specify missing
comment|// from zero-length?
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
comment|// test overwrite by default
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|// test no overwrites
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='8']"
argument_list|)
expr_stmt|;
comment|// test overwrite
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|// test global value mapping
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"map"
argument_list|,
literal|"quoted:QUOTED"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='QUOTED']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
comment|// test value mapping to empty (remove)
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"map"
argument_list|,
literal|"quoted:"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
comment|// test value mapping from empty
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"map"
argument_list|,
literal|":EMPTY"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
comment|// test multiple map rules
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"map"
argument_list|,
literal|":EMPTY"
argument_list|,
literal|"map"
argument_list|,
literal|"quoted:QUOTED"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='QUOTED']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
comment|// test indexing empty fields
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"f.str_s.keepEmpty"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='']"
argument_list|)
expr_stmt|;
comment|// test overriding the name of fields
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"fieldnames"
argument_list|,
literal|"id,my_s"
argument_list|,
literal|"header"
argument_list|,
literal|"true"
argument_list|,
literal|"f.my_s.map"
argument_list|,
literal|":EMPTY"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='my_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"count(//str[@name='str_s'])=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//str[@name='my_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//str[@name='my_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='my_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
comment|// test that header in file was skipped
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:id"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test loading file as if it didn't have a header
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"fieldnames"
argument_list|,
literal|"id,my_s"
argument_list|,
literal|"header"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:id"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='my_s'][.='quoted']"
argument_list|)
expr_stmt|;
comment|// test multi-valued fields via field splitting w/ mapping of subvalues
name|makeFile
argument_list|(
literal|"id,str_s\n"
operator|+
literal|"100,\"quoted\"\n"
operator|+
literal|"101,\"a,b,c\"\n"
operator|+
literal|"102,\"a,,b\"\n"
operator|+
literal|"103,\n"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"f.str_s.map"
argument_list|,
literal|":EMPTY"
argument_list|,
literal|"f.str_s.split"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[1][.='a']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[2][.='b']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[3][.='c']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[2][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
comment|// test alternate values for delimiters
name|makeFile
argument_list|(
literal|"id|str_s\n"
operator|+
literal|"100|^quoted^\n"
operator|+
literal|"101|a;'b';c\n"
operator|+
literal|"102|a;;b\n"
operator|+
literal|"103|\n"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"file"
argument_list|,
name|filename
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"separator"
argument_list|,
literal|"|"
argument_list|,
literal|"encapsulator"
argument_list|,
literal|"^"
argument_list|,
literal|"f.str_s.map"
argument_list|,
literal|":EMPTY"
argument_list|,
literal|"f.str_s.split"
argument_list|,
literal|"true"
argument_list|,
literal|"f.str_s.separator"
argument_list|,
literal|";"
argument_list|,
literal|"f.str_s.encapsulator"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:100"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='quoted']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[1][.='a']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[2][.='b']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:101"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[3][.='c']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:102"
argument_list|)
argument_list|,
literal|"//arr[@name='str_s']/str[2][.='EMPTY']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:103"
argument_list|)
argument_list|,
literal|"//str[@name='str_s'][.='EMPTY']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
