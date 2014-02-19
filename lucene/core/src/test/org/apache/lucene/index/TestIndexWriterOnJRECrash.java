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
comment|/**  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|List
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
name|BaseDirectoryWrapper
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
name|TestUtil
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SeedUtils
import|;
end_import
begin_comment
comment|/**  * Runs TestNRTThreads in a separate process, crashes the JRE in the middle  * of execution, then runs checkindex to make sure its not corrupt.  */
end_comment
begin_class
DECL|class|TestIndexWriterOnJRECrash
specifier|public
class|class
name|TestIndexWriterOnJRECrash
extends|extends
name|TestNRTThreads
block|{
DECL|field|tempDir
specifier|private
name|File
name|tempDir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|tempDir
operator|=
name|TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"jrecrash"
argument_list|)
expr_stmt|;
name|tempDir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nightly
DECL|method|testNRTThreads
specifier|public
name|void
name|testNRTThreads
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if we are not the fork
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.crashmode"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// try up to 10 times to create an index
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|forkTest
argument_list|()
expr_stmt|;
comment|// if we succeeded in finding an index, we are done.
if|if
condition|(
name|checkIndexes
argument_list|(
name|tempDir
argument_list|)
condition|)
return|return;
block|}
block|}
else|else
block|{
comment|// note: re-enable this if we create a 4.x impersonator,
comment|// and if its format is actually different than the real 4.x (unlikely)
comment|// TODO: the non-fork code could simply enable impersonation?
comment|// assumeFalse("does not support PreFlex, see LUCENE-3992",
comment|//    Codec.getDefault().getName().equals("Lucene4x"));
comment|// we are the fork, setup a crashing thread
specifier|final
name|int
name|crashTime
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3000
argument_list|,
literal|4000
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|crashTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|crashJRE
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// run the test until we crash.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|super
operator|.
name|testNRTThreads
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** fork ourselves in a new jvm. sets -Dtests.crashmode=true */
DECL|method|forkTest
specifier|public
name|void
name|forkTest
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"bin"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"java"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Xmx512m"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.crashmode=true"
argument_list|)
expr_stmt|;
comment|// passing NIGHTLY to this test makes it run for much longer, easier to catch it in the act...
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nightly=true"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-DtempDir="
operator|+
name|tempDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.seed="
operator|+
name|SeedUtils
operator|.
name|formatSeed
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-ea"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"org.junit.runner.JUnitCore"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Process
name|p
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
comment|// We pump everything to stderr.
name|PrintStream
name|childOut
init|=
name|System
operator|.
name|err
decl_stmt|;
name|Thread
name|stdoutPumper
init|=
name|ThreadPumper
operator|.
name|start
argument_list|(
name|p
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|childOut
argument_list|)
decl_stmt|;
name|Thread
name|stderrPumper
init|=
name|ThreadPumper
operator|.
name|start
argument_list|(
name|p
operator|.
name|getErrorStream
argument_list|()
argument_list|,
name|childOut
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|childOut
operator|.
name|println
argument_list|(
literal|">>> Begin subprocess output"
argument_list|)
expr_stmt|;
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|stdoutPumper
operator|.
name|join
argument_list|()
expr_stmt|;
name|stderrPumper
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|childOut
operator|.
name|println
argument_list|(
literal|"<<< End subprocess output"
argument_list|)
expr_stmt|;
block|}
comment|/** A pipe thread. It'd be nice to reuse guava's implementation for this... */
DECL|class|ThreadPumper
specifier|static
class|class
name|ThreadPumper
block|{
DECL|method|start
specifier|public
specifier|static
name|Thread
name|start
parameter_list|(
specifier|final
name|InputStream
name|from
parameter_list|,
specifier|final
name|OutputStream
name|to
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|from
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|to
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Couldn't pipe from the forked process: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
comment|/**    * Recursively looks for indexes underneath<code>file</code>,    * and runs checkindex on them. returns true if it found any indexes.    */
DECL|method|checkIndexes
specifier|public
name|boolean
name|checkIndexes
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|BaseDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't double-checkindex
if|if
condition|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Checking index: "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-4738: if we crashed while writing first
comment|// commit it's possible index will be corrupt (by
comment|// design we don't try to be smart about this case
comment|// since that too risky):
if|if
condition|(
name|SegmentInfos
operator|.
name|getLastCommitGeneration
argument_list|(
name|dir
argument_list|)
operator|>
literal|1
condition|)
block|{
name|TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|file
operator|.
name|listFiles
argument_list|()
control|)
if|if
condition|(
name|checkIndexes
argument_list|(
name|f
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * currently, this only works/tested on Sun and IBM.    */
DECL|method|crashJRE
specifier|public
name|void
name|crashJRE
parameter_list|()
block|{
specifier|final
name|String
name|vendor
init|=
name|Constants
operator|.
name|JAVA_VENDOR
decl_stmt|;
specifier|final
name|boolean
name|supportsUnsafeNpeDereference
init|=
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Oracle"
argument_list|)
operator|||
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Sun"
argument_list|)
operator|||
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Apple"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|supportsUnsafeNpeDereference
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"putAddress"
argument_list|,
name|long
operator|.
name|class
argument_list|,
name|long
operator|.
name|class
argument_list|)
decl_stmt|;
name|m
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't kill the JVM via Unsafe."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fallback attempt to Runtime.halt();
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|halt
argument_list|(
operator|-
literal|1
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
literal|"Couldn't kill the JVM."
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|// We couldn't get the JVM to crash for some reason.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
