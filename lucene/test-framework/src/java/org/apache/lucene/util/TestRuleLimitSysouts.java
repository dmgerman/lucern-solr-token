begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Inherited
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
operator|.
name|SuppressSysoutChecks
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
name|RandomizedTest
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
name|rules
operator|.
name|TestRuleAdapter
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Fails the suite if it prints over the given limit of bytes to either  * {@link System#out} or {@link System#err},  * unless the condition is not enforced (see {@link #isEnforced()}).  */
end_comment
begin_class
DECL|class|TestRuleLimitSysouts
specifier|public
class|class
name|TestRuleLimitSysouts
extends|extends
name|TestRuleAdapter
block|{
comment|/**    * Max limit of bytes printed to either {@link System#out} or {@link System#err}.     * This limit is enforced per-class (suite).    */
DECL|field|DEFAULT_SYSOUT_BYTES_THRESHOLD
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_SYSOUT_BYTES_THRESHOLD
init|=
literal|8
operator|*
literal|1024
decl_stmt|;
comment|/**    * An annotation specifying the limit of bytes per class.    */
annotation|@
name|Documented
annotation|@
name|Inherited
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
annotation|@
name|Target
argument_list|(
name|ElementType
operator|.
name|TYPE
argument_list|)
DECL|interface|Limit
specifier|public
specifier|static
annotation_defn|@interface
name|Limit
block|{
DECL|method|bytes
specifier|public
name|int
name|bytes
parameter_list|()
function_decl|;
block|}
DECL|field|bytesWritten
specifier|private
specifier|final
specifier|static
name|AtomicInteger
name|bytesWritten
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|capturedSystemOut
specifier|private
specifier|final
specifier|static
name|DelegateStream
name|capturedSystemOut
decl_stmt|;
DECL|field|capturedSystemErr
specifier|private
specifier|final
specifier|static
name|DelegateStream
name|capturedSystemErr
decl_stmt|;
comment|/**    * We capture system output and error streams as early as possible because    * certain components (like the Java logging system) steal these references and    * never refresh them.    *     * Also, for this exact reason, we cannot change delegate streams for every suite.    * This isn't as elegant as it should be, but there's no workaround for this.    */
static|static
block|{
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|csn
init|=
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|capturedSystemOut
operator|=
operator|new
name|DelegateStream
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|csn
argument_list|,
name|bytesWritten
argument_list|)
expr_stmt|;
name|capturedSystemErr
operator|=
operator|new
name|DelegateStream
argument_list|(
name|System
operator|.
name|err
argument_list|,
name|csn
argument_list|,
name|bytesWritten
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|capturedSystemOut
operator|.
name|printStream
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|capturedSystemErr
operator|.
name|printStream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test failures from any tests or rules before.    */
DECL|field|failureMarker
specifier|private
specifier|final
name|TestRuleMarkFailure
name|failureMarker
decl_stmt|;
comment|/**    * Tracks the number of bytes written to an underlying stream by    * incrementing an {@link AtomicInteger}.    */
DECL|class|DelegateStream
specifier|static
class|class
name|DelegateStream
extends|extends
name|FilterOutputStream
block|{
DECL|field|printStream
specifier|final
name|PrintStream
name|printStream
decl_stmt|;
DECL|field|bytesCounter
specifier|final
name|AtomicInteger
name|bytesCounter
decl_stmt|;
DECL|method|DelegateStream
specifier|public
name|DelegateStream
parameter_list|(
name|OutputStream
name|delegate
parameter_list|,
name|String
name|charset
parameter_list|,
name|AtomicInteger
name|bytesCounter
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|printStream
operator|=
operator|new
name|PrintStream
argument_list|(
name|this
argument_list|,
literal|true
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesCounter
operator|=
name|bytesCounter
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
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
comment|// Do override all three write() methods to make sure nothing slips through.
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|bytesCounter
operator|.
name|addAndGet
argument_list|(
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|bytesCounter
operator|.
name|addAndGet
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|bytesCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|super
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|TestRuleLimitSysouts
specifier|public
name|TestRuleLimitSysouts
parameter_list|(
name|TestRuleMarkFailure
name|failureMarker
parameter_list|)
block|{
name|this
operator|.
name|failureMarker
operator|=
name|failureMarker
expr_stmt|;
block|}
comment|/** */
annotation|@
name|Override
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|isEnforced
argument_list|()
condition|)
block|{
name|checkCaptureStreams
argument_list|()
expr_stmt|;
block|}
name|resetCaptureState
argument_list|()
expr_stmt|;
name|validateClassAnnotations
argument_list|()
expr_stmt|;
block|}
DECL|method|validateClassAnnotations
specifier|private
name|void
name|validateClassAnnotations
parameter_list|()
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|target
init|=
name|RandomizedTest
operator|.
name|getContext
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|.
name|isAnnotationPresent
argument_list|(
name|Limit
operator|.
name|class
argument_list|)
condition|)
block|{
name|int
name|bytes
init|=
name|target
operator|.
name|getAnnotation
argument_list|(
name|Limit
operator|.
name|class
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
argument_list|<
literal|0
operator|||
name|bytes
argument_list|>
literal|1
operator|*
literal|1024
operator|*
literal|1024
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"The sysout limit is insane. Did you want to use "
operator|+
literal|"@"
operator|+
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" annotation to "
operator|+
literal|"avoid sysout checks entirely?"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Ensures {@link System#out} and {@link System#err} point to delegate streams.    */
DECL|method|checkCaptureStreams
specifier|public
specifier|static
name|void
name|checkCaptureStreams
parameter_list|()
block|{
comment|// Make sure we still hold the right references to wrapper streams.
if|if
condition|(
name|System
operator|.
name|out
operator|!=
name|capturedSystemOut
operator|.
name|printStream
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Something has changed System.out to: "
operator|+
name|System
operator|.
name|out
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|System
operator|.
name|err
operator|!=
name|capturedSystemErr
operator|.
name|printStream
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Something has changed System.err to: "
operator|+
name|System
operator|.
name|err
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|isEnforced
specifier|protected
name|boolean
name|isEnforced
parameter_list|()
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|target
init|=
name|RandomizedTest
operator|.
name|getContext
argument_list|()
operator|.
name|getTargetClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
operator|||
name|LuceneTestCase
operator|.
name|INFOSTREAM
operator|||
name|target
operator|.
name|isAnnotationPresent
argument_list|(
name|SuppressSysoutChecks
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|target
operator|.
name|isAnnotationPresent
argument_list|(
name|Limit
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * We're only interested in failing the suite if it was successful (otherwise    * just propagate the original problem and don't bother doing anything else).    */
annotation|@
name|Override
DECL|method|afterIfSuccessful
specifier|protected
name|void
name|afterIfSuccessful
parameter_list|()
throws|throws
name|Throwable
block|{
if|if
condition|(
name|isEnforced
argument_list|()
condition|)
block|{
name|checkCaptureStreams
argument_list|()
expr_stmt|;
comment|// Flush any buffers.
name|capturedSystemOut
operator|.
name|printStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|capturedSystemErr
operator|.
name|printStream
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// Check for offenders, but only if everything was successful so far.
name|int
name|limit
init|=
name|RandomizedTest
operator|.
name|getContext
argument_list|()
operator|.
name|getTargetClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|Limit
operator|.
name|class
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesWritten
operator|.
name|get
argument_list|()
operator|>=
name|limit
operator|&&
name|failureMarker
operator|.
name|wasSuccessful
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"The test or suite printed %d bytes to stdout and stderr,"
operator|+
literal|" even though the limit was set to %d bytes. Increase the limit with @%s, ignore it completely"
operator|+
literal|" with @%s or run with -Dtests.verbose=true"
argument_list|,
name|bytesWritten
operator|.
name|get
argument_list|()
argument_list|,
name|limit
argument_list|,
name|Limit
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|SuppressSysoutChecks
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|afterAlways
specifier|protected
name|void
name|afterAlways
parameter_list|(
name|List
argument_list|<
name|Throwable
argument_list|>
name|errors
parameter_list|)
throws|throws
name|Throwable
block|{
name|resetCaptureState
argument_list|()
expr_stmt|;
block|}
DECL|method|resetCaptureState
specifier|private
name|void
name|resetCaptureState
parameter_list|()
block|{
name|capturedSystemOut
operator|.
name|printStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|capturedSystemErr
operator|.
name|printStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|bytesWritten
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
