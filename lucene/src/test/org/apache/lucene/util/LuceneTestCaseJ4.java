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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|LogDocMergePolicy
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
name|index
operator|.
name|LogMergePolicy
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
name|index
operator|.
name|SerialMergeScheduler
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|FieldCache
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
name|search
operator|.
name|FieldCache
operator|.
name|CacheEntry
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
name|FieldCacheSanityChecker
operator|.
name|Insanity
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|index
operator|.
name|codecs
operator|.
name|Codec
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
name|index
operator|.
name|codecs
operator|.
name|preflexrw
operator|.
name|PreFlexRWCodec
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestWatchman
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|FrameworkMethod
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
name|PrintStream
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
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import
begin_comment
comment|/**  * Base class for all Lucene unit tests, Junit4 variant.  * Replaces LuceneTestCase.  *<p>  *</p>  *<p>  * If you  * override either<code>setUp()</code> or  *<code>tearDown()</code> in your unit test, make sure you  * call<code>super.setUp()</code> and  *<code>super.tearDown()</code>  *</p>  *  * @After - replaces setup  * @Before - replaces teardown  * @Test - any public method with this annotation is a test case, regardless  * of its name  *<p>  *<p>  * See Junit4<a href="http://junit.org/junit/javadoc/4.7/">documentation</a> for a complete list of features.  *<p>  * Import from org.junit rather than junit.framework.  *<p>  * You should be able to use this class anywhere you used LuceneTestCase  * if you annotate your derived class correctly with the annotations above  * @see #assertSaneFieldCaches(String)  */
end_comment
begin_comment
comment|// If we really need functionality in runBare override from LuceneTestCase,
end_comment
begin_comment
comment|// we can introduce RunBareWrapper and override runChild, and add the
end_comment
begin_comment
comment|// @RunWith annotation as below. runChild will be called for
end_comment
begin_comment
comment|// every test. But the functionality we used to
end_comment
begin_comment
comment|// get from that override is provided by InterceptTestCaseEvents
end_comment
begin_comment
comment|//@RunWith(RunBareWrapper.class)
end_comment
begin_class
DECL|class|LuceneTestCaseJ4
specifier|public
class|class
name|LuceneTestCaseJ4
block|{
comment|/**    * true iff tests are run in verbose mode. Note: if it is false, tests are not    * expected to print any messages.    */
DECL|field|VERBOSE
specifier|public
specifier|static
specifier|final
name|boolean
name|VERBOSE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tests.verbose"
argument_list|)
decl_stmt|;
comment|/** Use this constant when creating Analyzers and any other version-dependent stuff.    *<p><b>NOTE:</b> Change this when development starts for new Lucene version:    */
DECL|field|TEST_VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|TEST_VERSION_CURRENT
init|=
name|Version
operator|.
name|LUCENE_40
decl_stmt|;
comment|/** Create indexes in this directory, optimally use a subdir, named after the test */
DECL|field|TEMP_DIR
specifier|public
specifier|static
specifier|final
name|File
name|TEMP_DIR
decl_stmt|;
static|static
block|{
name|String
name|s
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"To run tests, you need to define system property 'tempDir' or 'java.io.tmpdir'."
argument_list|)
throw|;
name|TEMP_DIR
operator|=
operator|new
name|File
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|field|savedBoolMaxClauseCount
specifier|private
name|int
name|savedBoolMaxClauseCount
decl_stmt|;
DECL|field|savedUncaughtExceptionHandler
specifier|private
specifier|volatile
name|Thread
operator|.
name|UncaughtExceptionHandler
name|savedUncaughtExceptionHandler
init|=
literal|null
decl_stmt|;
comment|/** Used to track if setUp and tearDown are called correctly from subclasses */
DECL|field|setup
specifier|private
name|boolean
name|setup
decl_stmt|;
DECL|class|UncaughtExceptionEntry
specifier|private
specifier|static
class|class
name|UncaughtExceptionEntry
block|{
DECL|field|thread
specifier|public
specifier|final
name|Thread
name|thread
decl_stmt|;
DECL|field|exception
specifier|public
specifier|final
name|Throwable
name|exception
decl_stmt|;
DECL|method|UncaughtExceptionEntry
specifier|public
name|UncaughtExceptionEntry
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|Throwable
name|exception
parameter_list|)
block|{
name|this
operator|.
name|thread
operator|=
name|thread
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
block|}
DECL|field|uncaughtExceptions
specifier|private
name|List
argument_list|<
name|UncaughtExceptionEntry
argument_list|>
name|uncaughtExceptions
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|UncaughtExceptionEntry
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// checks if class correctly annotated
DECL|field|PLACEHOLDER
specifier|private
specifier|static
specifier|final
name|Object
name|PLACEHOLDER
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|checkedClasses
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
argument_list|,
name|Object
argument_list|>
name|checkedClasses
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// saves default codec: we do this statically as many build indexes in @beforeClass
DECL|field|savedDefaultCodec
specifier|private
specifier|static
name|String
name|savedDefaultCodec
decl_stmt|;
DECL|field|codec
specifier|private
specifier|static
name|String
name|codec
decl_stmt|;
DECL|field|preFlexSav
specifier|private
specifier|static
name|Codec
name|preFlexSav
decl_stmt|;
comment|// returns current PreFlex codec
DECL|method|installPreFlexRW
specifier|public
specifier|static
name|Codec
name|installPreFlexRW
parameter_list|()
block|{
specifier|final
name|Codec
name|preFlex
init|=
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"PreFlex"
argument_list|)
decl_stmt|;
if|if
condition|(
name|preFlex
operator|!=
literal|null
condition|)
block|{
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|unregister
argument_list|(
name|preFlex
argument_list|)
expr_stmt|;
block|}
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|register
argument_list|(
operator|new
name|PreFlexRWCodec
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|preFlex
return|;
block|}
comment|// returns current PreFlex codec
DECL|method|restorePreFlex
specifier|public
specifier|static
name|void
name|restorePreFlex
parameter_list|(
name|Codec
name|preFlex
parameter_list|)
block|{
name|Codec
name|preFlexRW
init|=
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|lookup
argument_list|(
literal|"PreFlex"
argument_list|)
decl_stmt|;
if|if
condition|(
name|preFlexRW
operator|!=
literal|null
condition|)
block|{
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|unregister
argument_list|(
name|preFlexRW
argument_list|)
expr_stmt|;
block|}
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|register
argument_list|(
name|preFlex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassLuceneTestCaseJ4
specifier|public
specifier|static
name|void
name|beforeClassLuceneTestCaseJ4
parameter_list|()
block|{
name|savedDefaultCodec
operator|=
name|CodecProvider
operator|.
name|getDefaultCodec
argument_list|()
expr_stmt|;
name|codec
operator|=
name|_TestUtil
operator|.
name|getTestCodec
argument_list|()
expr_stmt|;
if|if
condition|(
name|codec
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
name|codec
operator|=
name|CodecProvider
operator|.
name|CORE_CODECS
index|[
name|seedRnd
operator|.
name|nextInt
argument_list|(
name|CodecProvider
operator|.
name|CORE_CODECS
operator|.
name|length
argument_list|)
index|]
expr_stmt|;
comment|// If we're running w/ PreFlex codec we must swap in the
comment|// test-only PreFlexRW codec (since core PreFlex can
comment|// only read segments):
if|if
condition|(
name|codec
operator|.
name|equals
argument_list|(
literal|"PreFlex"
argument_list|)
condition|)
block|{
name|preFlexSav
operator|=
name|installPreFlexRW
argument_list|()
expr_stmt|;
block|}
name|CodecProvider
operator|.
name|setDefaultCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassLuceneTestCaseJ4
specifier|public
specifier|static
name|void
name|afterClassLuceneTestCaseJ4
parameter_list|()
block|{
comment|// Restore read-only PreFlex codec:
if|if
condition|(
name|codec
operator|.
name|equals
argument_list|(
literal|"PreFlex"
argument_list|)
condition|)
block|{
name|restorePreFlex
argument_list|(
name|preFlexSav
argument_list|)
expr_stmt|;
block|}
name|CodecProvider
operator|.
name|setDefaultCodec
argument_list|(
name|savedDefaultCodec
argument_list|)
expr_stmt|;
block|}
comment|// This is how we get control when errors occur.
comment|// Think of this as start/end/success/failed
comment|// events.
annotation|@
name|Rule
DECL|field|intercept
specifier|public
specifier|final
name|TestWatchman
name|intercept
init|=
operator|new
name|TestWatchman
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|failed
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|FrameworkMethod
name|method
parameter_list|)
block|{
name|reportAdditionalFailureInfo
argument_list|()
expr_stmt|;
name|super
operator|.
name|failed
argument_list|(
name|e
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|starting
parameter_list|(
name|FrameworkMethod
name|method
parameter_list|)
block|{
comment|// set current method name for logging
name|LuceneTestCaseJ4
operator|.
name|this
operator|.
name|name
operator|=
name|method
operator|.
name|getName
argument_list|()
expr_stmt|;
comment|// check if the current test's class annotated all test* methods with @Test
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
name|clazz
init|=
name|LuceneTestCaseJ4
operator|.
name|this
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|checkedClasses
operator|.
name|containsKey
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|checkedClasses
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|PLACEHOLDER
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|clazz
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"test"
argument_list|)
operator|&&
name|m
operator|.
name|getAnnotation
argument_list|(
name|Test
operator|.
name|class
argument_list|)
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"In class '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"' the method '"
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not annotated with @Test."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|super
operator|.
name|starting
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"ensure your tearDown() calls super.tearDown()!!!"
argument_list|,
name|setup
argument_list|)
expr_stmt|;
name|setup
operator|=
literal|true
expr_stmt|;
name|savedUncaughtExceptionHandler
operator|=
name|Thread
operator|.
name|getDefaultUncaughtExceptionHandler
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|uncaughtExceptions
operator|.
name|add
argument_list|(
operator|new
name|UncaughtExceptionEntry
argument_list|(
name|t
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|savedUncaughtExceptionHandler
operator|!=
literal|null
condition|)
name|savedUncaughtExceptionHandler
operator|.
name|uncaughtException
argument_list|(
name|t
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ConcurrentMergeScheduler
operator|.
name|setTestMode
argument_list|()
expr_stmt|;
name|savedBoolMaxClauseCount
operator|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
expr_stmt|;
name|seed
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Forcible purges all cache entries from the FieldCache.    *<p>    * This method will be called by tearDown to clean up FieldCache.DEFAULT.    * If a (poorly written) test has some expectation that the FieldCache    * will persist across test methods (ie: a static IndexReader) this    * method can be overridden to do nothing.    *</p>    *    * @see FieldCache#purgeAllCaches()    */
DECL|method|purgeFieldCache
specifier|protected
name|void
name|purgeFieldCache
parameter_list|(
specifier|final
name|FieldCache
name|fc
parameter_list|)
block|{
name|fc
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|getTestLabel
specifier|protected
name|String
name|getTestLabel
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"ensure your setUp() calls super.setUp()!!!"
argument_list|,
name|setup
argument_list|)
expr_stmt|;
name|setup
operator|=
literal|false
expr_stmt|;
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|savedBoolMaxClauseCount
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|uncaughtExceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"The following exceptions were thrown by threads:"
argument_list|)
expr_stmt|;
for|for
control|(
name|UncaughtExceptionEntry
name|entry
range|:
name|uncaughtExceptions
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"*** Thread: "
operator|+
name|entry
operator|.
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|exception
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Some threads threw uncaught exceptions!"
argument_list|)
expr_stmt|;
block|}
comment|// calling assertSaneFieldCaches here isn't as useful as having test
comment|// classes call it directly from the scope where the index readers
comment|// are used, because they could be gc'ed just before this tearDown
comment|// method is called.
comment|//
comment|// But it's better then nothing.
comment|//
comment|// If you are testing functionality that you know for a fact
comment|// "violates" FieldCache sanity, then you should either explicitly
comment|// call purgeFieldCache at the end of your test method, or refactor
comment|// your Test class so that the inconsistant FieldCache usages are
comment|// isolated in distinct test methods
name|assertSaneFieldCaches
argument_list|(
name|getTestLabel
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ConcurrentMergeScheduler
operator|.
name|anyUnhandledExceptions
argument_list|()
condition|)
block|{
comment|// Clear the failure so that we don't just keep
comment|// failing subsequent test cases
name|ConcurrentMergeScheduler
operator|.
name|clearUnhandledExceptions
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"ConcurrentMergeScheduler hit unhandled exceptions"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|purgeFieldCache
argument_list|(
name|FieldCache
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
name|savedUncaughtExceptionHandler
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that FieldCacheSanityChecker does not detect any    * problems with FieldCache.DEFAULT.    *<p>    * If any problems are found, they are logged to System.err    * (allong with the msg) when the Assertion is thrown.    *</p>    *<p>    * This method is called by tearDown after every test method,    * however IndexReaders scoped inside test methods may be garbage    * collected prior to this method being called, causing errors to    * be overlooked. Tests are encouraged to keep their IndexReaders    * scoped at the class level, or to explicitly call this method    * directly in the same scope as the IndexReader.    *</p>    *    * @see FieldCacheSanityChecker    */
DECL|method|assertSaneFieldCaches
specifier|protected
name|void
name|assertSaneFieldCaches
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
specifier|final
name|CacheEntry
index|[]
name|entries
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getCacheEntries
argument_list|()
decl_stmt|;
name|Insanity
index|[]
name|insanity
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|insanity
operator|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|dumpArray
argument_list|(
name|msg
operator|+
literal|": FieldCache"
argument_list|,
name|entries
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|assertEquals
argument_list|(
name|msg
operator|+
literal|": Insane FieldCache usage(s) found"
argument_list|,
literal|0
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|insanity
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
comment|// report this in the event of any exception/failure
comment|// if no failure, then insanity will be null anyway
if|if
condition|(
literal|null
operator|!=
name|insanity
condition|)
block|{
name|dumpArray
argument_list|(
name|msg
operator|+
literal|": Insane FieldCache usage(s)"
argument_list|,
name|insanity
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Convinience method for logging an iterator.    *    * @param label  String logged before/after the items in the iterator    * @param iter   Each next() is toString()ed and logged on it's own line. If iter is null this is logged differnetly then an empty iterator.    * @param stream Stream to log messages to.    */
DECL|method|dumpIterator
specifier|public
specifier|static
name|void
name|dumpIterator
parameter_list|(
name|String
name|label
parameter_list|,
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
parameter_list|,
name|PrintStream
name|stream
parameter_list|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|"*** BEGIN "
operator|+
name|label
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|iter
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
literal|" ... NULL ..."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|stream
operator|.
name|println
argument_list|(
name|iter
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|stream
operator|.
name|println
argument_list|(
literal|"*** END "
operator|+
name|label
operator|+
literal|" ***"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convinience method for logging an array.  Wraps the array in an iterator and delegates    *    * @see #dumpIterator(String,Iterator,PrintStream)    */
DECL|method|dumpArray
specifier|public
specifier|static
name|void
name|dumpArray
parameter_list|(
name|String
name|label
parameter_list|,
name|Object
index|[]
name|objs
parameter_list|,
name|PrintStream
name|stream
parameter_list|)
block|{
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
init|=
operator|(
literal|null
operator|==
name|objs
operator|)
condition|?
literal|null
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|objs
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|dumpIterator
argument_list|(
name|label
argument_list|,
name|iter
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * The random seed is logged during test execution and printed to System.out on any failure    * for reproducing the test using {@link #newRandom(long)} with the recorded seed    * .    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|()
block|{
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCaseJ4.newRandom only once per test"
argument_list|)
throw|;
block|}
name|this
operator|.
name|seed
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|seedRnd
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * If an error occurs in the test that is not reproducible, you can use this method to    * initialize the number generator with the seed that was printed out during the failing test.    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCaseJ4.newRandom only once per test"
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: random seed of testcase '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' is fixed to: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|seed
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
DECL|field|staticSeeds
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
argument_list|,
name|Long
argument_list|>
name|staticSeeds
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
argument_list|,
name|Long
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Returns a {@link Random} instance for generating random numbers from a beforeclass    * annotated method.    * The random seed is logged during test execution and printed to System.out on any failure    * for reproducing the test using {@link #newStaticRandom(Class, long)} with the recorded seed    * .    */
DECL|method|newStaticRandom
specifier|public
specifier|static
name|Random
name|newStaticRandom
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
name|clazz
parameter_list|)
block|{
name|Long
name|seed
init|=
name|seedRnd
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|staticSeeds
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|seed
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers from a beforeclass    * annotated method.    * If an error occurs in the test that is not reproducible, you can use this method to    * initialize the number generator with the seed that was printed out during the failing test.    */
DECL|method|newStaticRandom
specifier|public
specifier|static
name|Random
name|newStaticRandom
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|LuceneTestCaseJ4
argument_list|>
name|clazz
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|staticSeeds
operator|.
name|put
argument_list|(
name|clazz
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: random static seed of testclass '"
operator|+
name|clazz
operator|+
literal|"' is fixed to: "
operator|+
name|seed
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
comment|/** create a new index writer config with random defaults */
DECL|method|newIndexWriterConfig
specifier|public
specifier|static
name|IndexWriterConfig
name|newIndexWriterConfig
parameter_list|(
name|Random
name|r
parameter_list|,
name|Version
name|v
parameter_list|,
name|Analyzer
name|a
parameter_list|)
block|{
name|IndexWriterConfig
name|c
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|v
argument_list|,
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogDocMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|setTermIndexInterval
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|LogMergePolicy
name|logmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|c
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|logmp
operator|.
name|setUseCompoundDocStore
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|logmp
operator|.
name|setUseCompoundFile
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|logmp
operator|.
name|setCalibrateSizeByDeletes
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|logmp
operator|.
name|setMergeFactor
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|setReaderPooling
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/** Gets a resource from the classpath as {@link File}. This method should only be used,    * if a real file is needed. To get a stream, code should prefer    * {@link Class#getResourceAsStream} using {@code this.getClass()}.    */
DECL|method|getDataFile
specifier|protected
name|File
name|getDataFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|new
name|File
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find resource: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
comment|// We get here from InterceptTestCaseEvents on the 'failed' event....
DECL|method|reportAdditionalFailureInfo
specifier|public
name|void
name|reportAdditionalFailureInfo
parameter_list|()
block|{
name|Long
name|staticSeed
init|=
name|staticSeeds
operator|.
name|get
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|staticSeed
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: random static seed of testclass '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' was: "
operator|+
name|staticSeed
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|_TestUtil
operator|.
name|getTestCodec
argument_list|()
operator|.
name|equals
argument_list|(
literal|"random"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: random codec of testcase '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' was: "
operator|+
name|codec
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: random seed of testcase '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' was: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
comment|// recorded seed
DECL|field|seed
specifier|protected
name|Long
name|seed
init|=
literal|null
decl_stmt|;
comment|// static members
DECL|field|seedRnd
specifier|private
specifier|static
specifier|final
name|Random
name|seedRnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|"<unknown>"
decl_stmt|;
block|}
end_class
end_unit
