begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|FSDirectory
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
name|CommandLineUtil
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
name|Constants
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
name|InfoStream
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
name|Version
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_comment
comment|/**   * This is an easy-to-use tool that upgrades all segments of an index from previous Lucene versions   * to the current segment file format. It can be used from command line:   *<pre>   *  java -cp lucene-core.jar org.apache.lucene.index.IndexUpgrader [-delete-prior-commits] [-verbose] indexDir   *</pre>   * Alternatively this class can be instantiated and {@link #upgrade} invoked. It uses {@link UpgradeIndexMergePolicy}   * and triggers the upgrade via an forceMerge request to {@link IndexWriter}.   *<p>This tool keeps only the last commit in an index; for this   * reason, if the incoming index has more than one commit, the tool   * refuses to run by default. Specify {@code -delete-prior-commits}   * to override this, allowing the tool to delete all but the last commit.   * From Java code this can be enabled by passing {@code true} to   * {@link #IndexUpgrader(Directory,Version,PrintStream,boolean)}.   *<p><b>Warning:</b> This tool may reorder documents if the index was partially   * upgraded before execution (e.g., documents were added). If your application relies   * on&quot;monotonicity&quot; of doc IDs (which means that the order in which the documents   * were added to the index is preserved), do a full forceMerge instead.   * The {@link MergePolicy} set by {@link IndexWriterConfig} may also reorder   * documents.   */
end_comment
begin_class
DECL|class|IndexUpgrader
specifier|public
specifier|final
class|class
name|IndexUpgrader
block|{
DECL|method|printUsage
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Upgrades an index so all segments created with a previous Lucene version are rewritten."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  java "
operator|+
name|IndexUpgrader
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" [-delete-prior-commits] [-verbose] [-dir-impl X] indexDir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"This tool keeps only the last commit in an index; for this"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"reason, if the incoming index has more than one commit, the tool"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"refuses to run by default. Specify -delete-prior-commits to override"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"this, allowing the tool to delete all but the last commit."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Specify a "
operator|+
name|FSDirectory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" implementation through the -dir-impl option to force its use. If no package is specified the "
operator|+
name|FSDirectory
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" package will be used."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: This tool may reorder document IDs!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Main method to run {code IndexUpgrader} from the    *  command-line. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
name|IOException
block|{
name|parseArgs
argument_list|(
name|args
argument_list|)
operator|.
name|upgrade
argument_list|()
expr_stmt|;
block|}
DECL|method|parseArgs
specifier|static
name|IndexUpgrader
name|parseArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
name|boolean
name|deletePriorCommits
init|=
literal|false
decl_stmt|;
name|PrintStream
name|out
init|=
literal|null
decl_stmt|;
name|String
name|dirImpl
init|=
literal|null
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|args
operator|.
name|length
condition|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
literal|"-delete-prior-commits"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|deletePriorCommits
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-verbose"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|out
operator|=
name|System
operator|.
name|out
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-dir-impl"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|args
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: missing value for -dir-impl option"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
name|dirImpl
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|arg
expr_stmt|;
block|}
else|else
block|{
name|printUsage
argument_list|()
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
block|}
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dirImpl
operator|==
literal|null
condition|)
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
name|CommandLineUtil
operator|.
name|newFSDirectory
argument_list|(
name|dirImpl
argument_list|,
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IndexUpgrader
argument_list|(
name|dir
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|out
argument_list|,
name|deletePriorCommits
argument_list|)
return|;
block|}
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|iwc
specifier|private
specifier|final
name|IndexWriterConfig
name|iwc
decl_stmt|;
DECL|field|deletePriorCommits
specifier|private
specifier|final
name|boolean
name|deletePriorCommits
decl_stmt|;
comment|/** Creates index upgrader on the given directory, using an {@link IndexWriter} using the given    * {@code matchVersion}. The tool refuses to upgrade indexes with multiple commit points. */
DECL|method|IndexUpgrader
specifier|public
name|IndexUpgrader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|matchVersion
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Creates index upgrader on the given directory, using an {@link IndexWriter} using the given    * {@code matchVersion}. You have the possibility to upgrade indexes with multiple commit points by removing    * all older ones. If {@code infoStream} is not {@code null}, all logging output will be sent to this stream. */
DECL|method|IndexUpgrader
specifier|public
name|IndexUpgrader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Version
name|matchVersion
parameter_list|,
name|PrintStream
name|infoStream
parameter_list|,
name|boolean
name|deletePriorCommits
parameter_list|)
block|{
name|this
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|matchVersion
argument_list|,
literal|null
argument_list|)
argument_list|,
name|deletePriorCommits
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|infoStream
condition|)
block|{
name|this
operator|.
name|iwc
operator|.
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Creates index upgrader on the given directory, using an {@link IndexWriter} using the given    * config. You have the possibility to upgrade indexes with multiple commit points by removing    * all older ones. */
DECL|method|IndexUpgrader
specifier|public
name|IndexUpgrader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexWriterConfig
name|iwc
parameter_list|,
name|boolean
name|deletePriorCommits
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|iwc
operator|=
name|iwc
expr_stmt|;
name|this
operator|.
name|deletePriorCommits
operator|=
name|deletePriorCommits
expr_stmt|;
block|}
comment|/** Perform the upgrade. */
DECL|method|upgrade
specifier|public
name|void
name|upgrade
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexNotFoundException
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|deletePriorCommits
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|IndexCommit
argument_list|>
name|commits
init|=
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|commits
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This tool was invoked to not delete prior commit points, but the following commits were found: "
operator|+
name|commits
argument_list|)
throw|;
block|}
block|}
specifier|final
name|IndexWriterConfig
name|c
init|=
name|iwc
operator|.
name|clone
argument_list|()
decl_stmt|;
name|c
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|UpgradeIndexMergePolicy
argument_list|(
name|c
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|c
argument_list|)
decl_stmt|;
try|try
block|{
name|InfoStream
name|infoStream
init|=
name|c
operator|.
name|getInfoStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"IndexUpgrader"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"IndexUpgrader"
argument_list|,
literal|"Upgrading all pre-"
operator|+
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
operator|+
literal|" segments of index directory '"
operator|+
name|dir
operator|+
literal|"' to version "
operator|+
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"IndexUpgrader"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"IndexUpgrader"
argument_list|,
literal|"All segments upgraded to version "
operator|+
name|Constants
operator|.
name|LUCENE_MAIN_VERSION
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
