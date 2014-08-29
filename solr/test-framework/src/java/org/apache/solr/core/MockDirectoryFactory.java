begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|IOException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MockDirectoryWrapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|NRTCachingDirectory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RateLimitedDirectoryWrapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|TrackingDirectoryWrapper
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Opens a directory with {@link LuceneTestCase#newDirectory()}  */
end_comment
begin_class
DECL|class|MockDirectoryFactory
specifier|public
class|class
name|MockDirectoryFactory
extends|extends
name|EphemeralDirectoryFactory
block|{
DECL|field|SOLR_TESTS_ALLOW_READING_FILES_STILL_OPEN_FOR_WRITE
specifier|public
specifier|static
specifier|final
name|String
name|SOLR_TESTS_ALLOW_READING_FILES_STILL_OPEN_FOR_WRITE
init|=
literal|"solr.tests.allow_reading_files_still_open_for_write"
decl_stmt|;
DECL|field|allowReadingFilesStillOpenForWrite
specifier|private
name|boolean
name|allowReadingFilesStillOpenForWrite
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|SOLR_TESTS_ALLOW_READING_FILES_STILL_OPEN_FOR_WRITE
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|LuceneTestCase
operator|.
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|cdir
init|=
name|reduce
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|cdir
operator|=
name|reduce
argument_list|(
name|cdir
argument_list|)
expr_stmt|;
name|cdir
operator|=
name|reduce
argument_list|(
name|cdir
argument_list|)
expr_stmt|;
if|if
condition|(
name|cdir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
name|MockDirectoryWrapper
name|mockDirWrapper
init|=
operator|(
name|MockDirectoryWrapper
operator|)
name|cdir
decl_stmt|;
comment|// we can't currently do this check because of how
comment|// Solr has to reboot a new Directory sometimes when replicating
comment|// or rolling back - the old directory is closed and the following
comment|// test assumes it can open an IndexWriter when that happens - we
comment|// have a new Directory for the same dir and still an open IW at
comment|// this point
name|mockDirWrapper
operator|.
name|setAssertNoUnrefencedFilesOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// ram dirs in cores that are restarted end up empty
comment|// and check index fails
name|mockDirWrapper
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// if we enable this, TestReplicationHandler fails when it
comment|// tries to write to index.properties after the file has
comment|// already been created.
name|mockDirWrapper
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// snappuller& co don't seem ready for this:
name|mockDirWrapper
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowReadingFilesStillOpenForWrite
condition|)
block|{
name|mockDirWrapper
operator|.
name|setAllowReadingFilesStillOpenForWrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dir
return|;
block|}
DECL|method|reduce
specifier|private
name|Directory
name|reduce
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|Directory
name|cdir
init|=
name|dir
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|NRTCachingDirectory
condition|)
block|{
name|cdir
operator|=
operator|(
operator|(
name|NRTCachingDirectory
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cdir
operator|instanceof
name|RateLimitedDirectoryWrapper
condition|)
block|{
name|cdir
operator|=
operator|(
operator|(
name|RateLimitedDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cdir
operator|instanceof
name|TrackingDirectoryWrapper
condition|)
block|{
name|cdir
operator|=
operator|(
operator|(
name|TrackingDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getDelegate
argument_list|()
expr_stmt|;
block|}
return|return
name|cdir
return|;
block|}
annotation|@
name|Override
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// TODO: kind of a hack - we don't know what the delegate is, so
comment|// we treat it as file based since this works on most ephem impls
return|return
operator|new
name|File
argument_list|(
name|path
argument_list|)
operator|.
name|isAbsolute
argument_list|()
return|;
block|}
block|}
end_class
end_unit
