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
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41PostingsFormat
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|DocumentsWriterPerThread
operator|.
name|IndexingChain
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
name|IndexWriter
operator|.
name|IndexReaderWarmer
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
operator|.
name|OpenMode
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
name|IndexSearcher
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
name|similarities
operator|.
name|Similarity
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
begin_comment
comment|/**  * Holds all the configuration used by {@link IndexWriter} with few setters for  * settings that can be changed on an {@link IndexWriter} instance "live".  *   * @since 4.0  */
end_comment
begin_class
DECL|class|LiveIndexWriterConfig
specifier|public
class|class
name|LiveIndexWriterConfig
block|{
DECL|field|analyzer
specifier|private
specifier|final
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|private
specifier|volatile
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|ramBufferSizeMB
specifier|private
specifier|volatile
name|double
name|ramBufferSizeMB
decl_stmt|;
DECL|field|maxBufferedDeleteTerms
specifier|private
specifier|volatile
name|int
name|maxBufferedDeleteTerms
decl_stmt|;
DECL|field|readerTermsIndexDivisor
specifier|private
specifier|volatile
name|int
name|readerTermsIndexDivisor
decl_stmt|;
DECL|field|mergedSegmentWarmer
specifier|private
specifier|volatile
name|IndexReaderWarmer
name|mergedSegmentWarmer
decl_stmt|;
DECL|field|termIndexInterval
specifier|private
specifier|volatile
name|int
name|termIndexInterval
decl_stmt|;
comment|// TODO: this should be private to the codec, not settable here
comment|// modified by IndexWriterConfig
comment|/** {@link IndexDeletionPolicy} controlling when commit    *  points are deleted. */
DECL|field|delPolicy
specifier|protected
specifier|volatile
name|IndexDeletionPolicy
name|delPolicy
decl_stmt|;
comment|/** {@link IndexCommit} that {@link IndexWriter} is    *  opened on. */
DECL|field|commit
specifier|protected
specifier|volatile
name|IndexCommit
name|commit
decl_stmt|;
comment|/** {@link OpenMode} that {@link IndexWriter} is opened    *  with. */
DECL|field|openMode
specifier|protected
specifier|volatile
name|OpenMode
name|openMode
decl_stmt|;
comment|/** {@link Similarity} to use when encoding norms. */
DECL|field|similarity
specifier|protected
specifier|volatile
name|Similarity
name|similarity
decl_stmt|;
comment|/** {@link MergeScheduler} to use for running merges. */
DECL|field|mergeScheduler
specifier|protected
specifier|volatile
name|MergeScheduler
name|mergeScheduler
decl_stmt|;
comment|/** Timeout when trying to obtain the write lock on init. */
DECL|field|writeLockTimeout
specifier|protected
specifier|volatile
name|long
name|writeLockTimeout
decl_stmt|;
comment|/** {@link IndexingChain} that determines how documents are    *  indexed. */
DECL|field|indexingChain
specifier|protected
specifier|volatile
name|IndexingChain
name|indexingChain
decl_stmt|;
comment|/** {@link Codec} used to write new segments. */
DECL|field|codec
specifier|protected
specifier|volatile
name|Codec
name|codec
decl_stmt|;
comment|/** {@link InfoStream} for debugging messages. */
DECL|field|infoStream
specifier|protected
specifier|volatile
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** {@link MergePolicy} for selecting merges. */
DECL|field|mergePolicy
specifier|protected
specifier|volatile
name|MergePolicy
name|mergePolicy
decl_stmt|;
comment|/** {@code DocumentsWriterPerThreadPool} to control how    *  threads are allocated to {@code DocumentsWriterPerThread}. */
DECL|field|indexerThreadPool
specifier|protected
specifier|volatile
name|DocumentsWriterPerThreadPool
name|indexerThreadPool
decl_stmt|;
comment|/** True if readers should be pooled. */
DECL|field|readerPooling
specifier|protected
specifier|volatile
name|boolean
name|readerPooling
decl_stmt|;
comment|/** {@link FlushPolicy} to control when segments are    *  flushed. */
DECL|field|flushPolicy
specifier|protected
specifier|volatile
name|FlushPolicy
name|flushPolicy
decl_stmt|;
comment|/** Sets the hard upper bound on RAM usage for a single    *  segment, after which the segment is forced to flush. */
DECL|field|perThreadHardLimitMB
specifier|protected
specifier|volatile
name|int
name|perThreadHardLimitMB
decl_stmt|;
comment|/** {@link Version} that {@link IndexWriter} should emulate. */
DECL|field|matchVersion
specifier|protected
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|// used by IndexWriterConfig
DECL|method|LiveIndexWriterConfig
name|LiveIndexWriterConfig
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
expr_stmt|;
name|maxBufferedDocs
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
expr_stmt|;
name|maxBufferedDeleteTerms
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
expr_stmt|;
name|readerTermsIndexDivisor
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_READER_TERMS_INDEX_DIVISOR
expr_stmt|;
name|mergedSegmentWarmer
operator|=
literal|null
expr_stmt|;
name|termIndexInterval
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
expr_stmt|;
comment|// TODO: this should be private to the codec, not settable here
name|delPolicy
operator|=
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
expr_stmt|;
name|commit
operator|=
literal|null
expr_stmt|;
name|openMode
operator|=
name|OpenMode
operator|.
name|CREATE_OR_APPEND
expr_stmt|;
name|similarity
operator|=
name|IndexSearcher
operator|.
name|getDefaultSimilarity
argument_list|()
expr_stmt|;
name|mergeScheduler
operator|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
expr_stmt|;
name|writeLockTimeout
operator|=
name|IndexWriterConfig
operator|.
name|WRITE_LOCK_TIMEOUT
expr_stmt|;
name|indexingChain
operator|=
name|DocumentsWriterPerThread
operator|.
name|defaultIndexingChain
expr_stmt|;
name|codec
operator|=
name|Codec
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|infoStream
operator|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|mergePolicy
operator|=
operator|new
name|TieredMergePolicy
argument_list|()
expr_stmt|;
name|flushPolicy
operator|=
operator|new
name|FlushByRamOrCountsPolicy
argument_list|()
expr_stmt|;
name|readerPooling
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_READER_POOLING
expr_stmt|;
name|indexerThreadPool
operator|=
operator|new
name|ThreadAffinityDocumentsWriterThreadPool
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_MAX_THREAD_STATES
argument_list|)
expr_stmt|;
name|perThreadHardLimitMB
operator|=
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_PER_THREAD_HARD_LIMIT_MB
expr_stmt|;
block|}
comment|/**    * Creates a new config that that handles the live {@link IndexWriter}    * settings.    */
DECL|method|LiveIndexWriterConfig
name|LiveIndexWriterConfig
parameter_list|(
name|IndexWriterConfig
name|config
parameter_list|)
block|{
name|maxBufferedDeleteTerms
operator|=
name|config
operator|.
name|getMaxBufferedDeleteTerms
argument_list|()
expr_stmt|;
name|maxBufferedDocs
operator|=
name|config
operator|.
name|getMaxBufferedDocs
argument_list|()
expr_stmt|;
name|mergedSegmentWarmer
operator|=
name|config
operator|.
name|getMergedSegmentWarmer
argument_list|()
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|config
operator|.
name|getRAMBufferSizeMB
argument_list|()
expr_stmt|;
name|readerTermsIndexDivisor
operator|=
name|config
operator|.
name|getReaderTermsIndexDivisor
argument_list|()
expr_stmt|;
name|termIndexInterval
operator|=
name|config
operator|.
name|getTermIndexInterval
argument_list|()
expr_stmt|;
name|matchVersion
operator|=
name|config
operator|.
name|matchVersion
expr_stmt|;
name|analyzer
operator|=
name|config
operator|.
name|getAnalyzer
argument_list|()
expr_stmt|;
name|delPolicy
operator|=
name|config
operator|.
name|getIndexDeletionPolicy
argument_list|()
expr_stmt|;
name|commit
operator|=
name|config
operator|.
name|getIndexCommit
argument_list|()
expr_stmt|;
name|openMode
operator|=
name|config
operator|.
name|getOpenMode
argument_list|()
expr_stmt|;
name|similarity
operator|=
name|config
operator|.
name|getSimilarity
argument_list|()
expr_stmt|;
name|mergeScheduler
operator|=
name|config
operator|.
name|getMergeScheduler
argument_list|()
expr_stmt|;
name|writeLockTimeout
operator|=
name|config
operator|.
name|getWriteLockTimeout
argument_list|()
expr_stmt|;
name|indexingChain
operator|=
name|config
operator|.
name|getIndexingChain
argument_list|()
expr_stmt|;
name|codec
operator|=
name|config
operator|.
name|getCodec
argument_list|()
expr_stmt|;
name|infoStream
operator|=
name|config
operator|.
name|getInfoStream
argument_list|()
expr_stmt|;
name|mergePolicy
operator|=
name|config
operator|.
name|getMergePolicy
argument_list|()
expr_stmt|;
name|indexerThreadPool
operator|=
name|config
operator|.
name|getIndexerThreadPool
argument_list|()
expr_stmt|;
name|readerPooling
operator|=
name|config
operator|.
name|getReaderPooling
argument_list|()
expr_stmt|;
name|flushPolicy
operator|=
name|config
operator|.
name|getFlushPolicy
argument_list|()
expr_stmt|;
name|perThreadHardLimitMB
operator|=
name|config
operator|.
name|getRAMPerThreadHardLimitMB
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the default analyzer to use for indexing documents. */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**    * Expert: set the interval between indexed terms. Large values cause less    * memory to be used by IndexReader, but slow random-access to terms. Small    * values cause more memory to be used by an IndexReader, and speed    * random-access to terms.    *<p>    * This parameter determines the amount of computation required per query    * term, regardless of the number of documents that contain that term. In    * particular, it is the maximum number of other terms that must be scanned    * before a term is located and its frequency and position information may be    * processed. In a large index with user-entered query terms, query processing    * time is likely to be dominated not by term lookup but rather by the    * processing of frequency and positional data. In a small index or when many    * uncommon query terms are generated (e.g., by wildcard queries) term lookup    * may become a dominant cost.    *<p>    * In particular,<code>numUniqueTerms/interval</code> terms are read into    * memory by an IndexReader, and, on average,<code>interval/2</code> terms    * must be scanned for each random term access.    *     *<p>    * Takes effect immediately, but only applies to newly flushed/merged    * segments.    *     *<p>    *<b>NOTE:</b> This parameter does not apply to all PostingsFormat implementations,    * including the default one in this release. It only makes sense for term indexes    * that are implemented as a fixed gap between terms. For example,     * {@link Lucene41PostingsFormat} implements the term index instead based upon how    * terms share prefixes. To configure its parameters (the minimum and maximum size    * for a block), you would instead use  {@link Lucene41PostingsFormat#Lucene41PostingsFormat(int, int)}.    * which can also be configured on a per-field basis:    *<pre class="prettyprint">    * //customize Lucene41PostingsFormat, passing minBlockSize=50, maxBlockSize=100    * final PostingsFormat tweakedPostings = new Lucene41PostingsFormat(50, 100);    * iwc.setCodec(new Lucene41Codec() {    *&#64;Override    *   public PostingsFormat getPostingsFormatForField(String field) {    *     if (field.equals("fieldWithTonsOfTerms"))    *       return tweakedPostings;    *     else    *       return super.getPostingsFormatForField(field);    *   }    * });    *</pre>    * Note that other implementations may have their own parameters, or no parameters at all.    *     * @see IndexWriterConfig#DEFAULT_TERM_INDEX_INTERVAL    */
DECL|method|setTermIndexInterval
specifier|public
name|LiveIndexWriterConfig
name|setTermIndexInterval
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
comment|// TODO: this should be private to the codec, not settable here
name|this
operator|.
name|termIndexInterval
operator|=
name|interval
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the interval between indexed terms.    *    * @see #setTermIndexInterval(int)    */
DECL|method|getTermIndexInterval
specifier|public
name|int
name|getTermIndexInterval
parameter_list|()
block|{
comment|// TODO: this should be private to the codec, not settable here
return|return
name|termIndexInterval
return|;
block|}
comment|/**    * Determines the minimal number of delete terms required before the buffered    * in-memory delete terms and queries are applied and flushed.    *<p>    * Disabled by default (writer flushes by RAM usage).    *<p>    * NOTE: This setting won't trigger a segment flush.    *     *<p>    * Takes effect immediately, but only the next time a document is added,    * updated or deleted.    *     * @throws IllegalArgumentException    *           if maxBufferedDeleteTerms is enabled but smaller than 1    *     * @see #setRAMBufferSizeMB    */
DECL|method|setMaxBufferedDeleteTerms
specifier|public
name|LiveIndexWriterConfig
name|setMaxBufferedDeleteTerms
parameter_list|(
name|int
name|maxBufferedDeleteTerms
parameter_list|)
block|{
if|if
condition|(
name|maxBufferedDeleteTerms
operator|!=
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDeleteTerms
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxBufferedDeleteTerms must at least be 1 when enabled"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxBufferedDeleteTerms
operator|=
name|maxBufferedDeleteTerms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the number of buffered deleted terms that will trigger a flush of all    * buffered deletes if enabled.    *    * @see #setMaxBufferedDeleteTerms(int)    */
DECL|method|getMaxBufferedDeleteTerms
specifier|public
name|int
name|getMaxBufferedDeleteTerms
parameter_list|()
block|{
return|return
name|maxBufferedDeleteTerms
return|;
block|}
comment|/**    * Determines the amount of RAM that may be used for buffering added documents    * and deletions before they are flushed to the Directory. Generally for    * faster indexing performance it's best to flush by RAM usage instead of    * document count and use as large a RAM buffer as you can.    *<p>    * When this is set, the writer will flush whenever buffered documents and    * deletions use this much RAM. Pass in    * {@link IndexWriterConfig#DISABLE_AUTO_FLUSH} to prevent triggering a flush    * due to RAM usage. Note that if flushing by document count is also enabled,    * then the flush will be triggered by whichever comes first.    *<p>    * The maximum RAM limit is inherently determined by the JVMs available    * memory. Yet, an {@link IndexWriter} session can consume a significantly    * larger amount of memory than the given RAM limit since this limit is just    * an indicator when to flush memory resident documents to the Directory.    * Flushes are likely happen concurrently while other threads adding documents    * to the writer. For application stability the available memory in the JVM    * should be significantly larger than the RAM buffer used for indexing.    *<p>    *<b>NOTE</b>: the account of RAM usage for pending deletions is only    * approximate. Specifically, if you delete by Query, Lucene currently has no    * way to measure the RAM usage of individual Queries so the accounting will    * under-estimate and you should compensate by either calling commit()    * periodically yourself, or by using {@link #setMaxBufferedDeleteTerms(int)}    * to flush and apply buffered deletes by count instead of RAM usage (for each    * buffered delete Query a constant number of bytes is used to estimate RAM    * usage). Note that enabling {@link #setMaxBufferedDeleteTerms(int)} will not    * trigger any segment flushes.    *<p>    *<b>NOTE</b>: It's not guaranteed that all memory resident documents are    * flushed once this limit is exceeded. Depending on the configured    * {@link FlushPolicy} only a subset of the buffered documents are flushed and    * therefore only parts of the RAM buffer is released.    *<p>    *     * The default value is {@link IndexWriterConfig#DEFAULT_RAM_BUFFER_SIZE_MB}.    *     *<p>    * Takes effect immediately, but only the next time a document is added,    * updated or deleted.    *     * @see IndexWriterConfig#setRAMPerThreadHardLimitMB(int)    *     * @throws IllegalArgumentException    *           if ramBufferSize is enabled but non-positive, or it disables    *           ramBufferSize when maxBufferedDocs is already disabled    */
DECL|method|setRAMBufferSizeMB
specifier|public
name|LiveIndexWriterConfig
name|setRAMBufferSizeMB
parameter_list|(
name|double
name|ramBufferSizeMB
parameter_list|)
block|{
if|if
condition|(
name|ramBufferSizeMB
operator|!=
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
operator|&&
name|ramBufferSizeMB
operator|<=
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ramBufferSize should be> 0.0 MB when enabled"
argument_list|)
throw|;
block|}
if|if
condition|(
name|ramBufferSizeMB
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDocs
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least one of ramBufferSize and maxBufferedDocs must be enabled"
argument_list|)
throw|;
block|}
name|this
operator|.
name|ramBufferSizeMB
operator|=
name|ramBufferSizeMB
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the value set by {@link #setRAMBufferSizeMB(double)} if enabled. */
DECL|method|getRAMBufferSizeMB
specifier|public
name|double
name|getRAMBufferSizeMB
parameter_list|()
block|{
return|return
name|ramBufferSizeMB
return|;
block|}
comment|/**    * Determines the minimal number of documents required before the buffered    * in-memory documents are flushed as a new Segment. Large values generally    * give faster indexing.    *     *<p>    * When this is set, the writer will flush every maxBufferedDocs added    * documents. Pass in {@link IndexWriterConfig#DISABLE_AUTO_FLUSH} to prevent    * triggering a flush due to number of buffered documents. Note that if    * flushing by RAM usage is also enabled, then the flush will be triggered by    * whichever comes first.    *     *<p>    * Disabled by default (writer flushes by RAM usage).    *     *<p>    * Takes effect immediately, but only the next time a document is added,    * updated or deleted.    *     * @see #setRAMBufferSizeMB(double)    * @throws IllegalArgumentException    *           if maxBufferedDocs is enabled but smaller than 2, or it disables    *           maxBufferedDocs when ramBufferSize is already disabled    */
DECL|method|setMaxBufferedDocs
specifier|public
name|LiveIndexWriterConfig
name|setMaxBufferedDocs
parameter_list|(
name|int
name|maxBufferedDocs
parameter_list|)
block|{
if|if
condition|(
name|maxBufferedDocs
operator|!=
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDocs
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxBufferedDocs must at least be 2 when enabled"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxBufferedDocs
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
operator|&&
name|ramBufferSizeMB
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least one of ramBufferSize and maxBufferedDocs must be enabled"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxBufferedDocs
operator|=
name|maxBufferedDocs
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the number of buffered added documents that will trigger a flush if    * enabled.    *    * @see #setMaxBufferedDocs(int)    */
DECL|method|getMaxBufferedDocs
specifier|public
name|int
name|getMaxBufferedDocs
parameter_list|()
block|{
return|return
name|maxBufferedDocs
return|;
block|}
comment|/**    * Set the merged segment warmer. See {@link IndexReaderWarmer}.    *     *<p>    * Takes effect on the next merge.    */
DECL|method|setMergedSegmentWarmer
specifier|public
name|LiveIndexWriterConfig
name|setMergedSegmentWarmer
parameter_list|(
name|IndexReaderWarmer
name|mergeSegmentWarmer
parameter_list|)
block|{
name|this
operator|.
name|mergedSegmentWarmer
operator|=
name|mergeSegmentWarmer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the current merged segment warmer. See {@link IndexReaderWarmer}. */
DECL|method|getMergedSegmentWarmer
specifier|public
name|IndexReaderWarmer
name|getMergedSegmentWarmer
parameter_list|()
block|{
return|return
name|mergedSegmentWarmer
return|;
block|}
comment|/**    * Sets the termsIndexDivisor passed to any readers that IndexWriter opens,    * for example when applying deletes or creating a near-real-time reader in    * {@link DirectoryReader#open(IndexWriter, boolean)}. If you pass -1, the    * terms index won't be loaded by the readers. This is only useful in advanced    * situations when you will only .next() through all terms; attempts to seek    * will hit an exception.    *     *<p>    * Takes effect immediately, but only applies to readers opened after this    * call    *<p>    *<b>NOTE:</b> divisor settings&gt; 1 do not apply to all PostingsFormat    * implementations, including the default one in this release. It only makes    * sense for terms indexes that can efficiently re-sample terms at load time.    */
DECL|method|setReaderTermsIndexDivisor
specifier|public
name|LiveIndexWriterConfig
name|setReaderTermsIndexDivisor
parameter_list|(
name|int
name|divisor
parameter_list|)
block|{
if|if
condition|(
name|divisor
operator|<=
literal|0
operator|&&
name|divisor
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"divisor must be>= 1, or -1 (got "
operator|+
name|divisor
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|readerTermsIndexDivisor
operator|=
name|divisor
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the {@code termInfosIndexDivisor}.    *     * @see #setReaderTermsIndexDivisor(int) */
DECL|method|getReaderTermsIndexDivisor
specifier|public
name|int
name|getReaderTermsIndexDivisor
parameter_list|()
block|{
return|return
name|readerTermsIndexDivisor
return|;
block|}
comment|/** Returns the {@link OpenMode} set by {@link IndexWriterConfig#setOpenMode(OpenMode)}. */
DECL|method|getOpenMode
specifier|public
name|OpenMode
name|getOpenMode
parameter_list|()
block|{
return|return
name|openMode
return|;
block|}
comment|/**    * Returns the {@link IndexDeletionPolicy} specified in    * {@link IndexWriterConfig#setIndexDeletionPolicy(IndexDeletionPolicy)} or    * the default {@link KeepOnlyLastCommitDeletionPolicy}/    */
DECL|method|getIndexDeletionPolicy
specifier|public
name|IndexDeletionPolicy
name|getIndexDeletionPolicy
parameter_list|()
block|{
return|return
name|delPolicy
return|;
block|}
comment|/**    * Returns the {@link IndexCommit} as specified in    * {@link IndexWriterConfig#setIndexCommit(IndexCommit)} or the default,    * {@code null} which specifies to open the latest index commit point.    */
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
comment|/**    * Expert: returns the {@link Similarity} implementation used by this    * {@link IndexWriter}.    */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
comment|/**    * Returns the {@link MergeScheduler} that was set by    * {@link IndexWriterConfig#setMergeScheduler(MergeScheduler)}.    */
DECL|method|getMergeScheduler
specifier|public
name|MergeScheduler
name|getMergeScheduler
parameter_list|()
block|{
return|return
name|mergeScheduler
return|;
block|}
comment|/**    * Returns allowed timeout when acquiring the write lock.    *    * @see IndexWriterConfig#setWriteLockTimeout(long)    */
DECL|method|getWriteLockTimeout
specifier|public
name|long
name|getWriteLockTimeout
parameter_list|()
block|{
return|return
name|writeLockTimeout
return|;
block|}
comment|/** Returns the current {@link Codec}. */
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
return|return
name|codec
return|;
block|}
comment|/**    * Returns the current MergePolicy in use by this writer.    *    * @see IndexWriterConfig#setMergePolicy(MergePolicy)    */
DECL|method|getMergePolicy
specifier|public
name|MergePolicy
name|getMergePolicy
parameter_list|()
block|{
return|return
name|mergePolicy
return|;
block|}
comment|/**    * Returns the configured {@link DocumentsWriterPerThreadPool} instance.    *     * @see IndexWriterConfig#setIndexerThreadPool(DocumentsWriterPerThreadPool)    * @return the configured {@link DocumentsWriterPerThreadPool} instance.    */
DECL|method|getIndexerThreadPool
name|DocumentsWriterPerThreadPool
name|getIndexerThreadPool
parameter_list|()
block|{
return|return
name|indexerThreadPool
return|;
block|}
comment|/**    * Returns the max number of simultaneous threads that may be indexing    * documents at once in IndexWriter.    */
DECL|method|getMaxThreadStates
specifier|public
name|int
name|getMaxThreadStates
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
operator|(
name|ThreadAffinityDocumentsWriterThreadPool
operator|)
name|indexerThreadPool
operator|)
operator|.
name|getMaxThreadStates
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|cce
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|cce
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns {@code true} if {@link IndexWriter} should pool readers even if    * {@link DirectoryReader#open(IndexWriter, boolean)} has not been called.    */
DECL|method|getReaderPooling
specifier|public
name|boolean
name|getReaderPooling
parameter_list|()
block|{
return|return
name|readerPooling
return|;
block|}
comment|/**    * Returns the indexing chain set on    * {@link IndexWriterConfig#setIndexingChain(IndexingChain)}.    */
DECL|method|getIndexingChain
name|IndexingChain
name|getIndexingChain
parameter_list|()
block|{
return|return
name|indexingChain
return|;
block|}
comment|/**    * Returns the max amount of memory each {@link DocumentsWriterPerThread} can    * consume until forcefully flushed.    *     * @see IndexWriterConfig#setRAMPerThreadHardLimitMB(int)    */
DECL|method|getRAMPerThreadHardLimitMB
specifier|public
name|int
name|getRAMPerThreadHardLimitMB
parameter_list|()
block|{
return|return
name|perThreadHardLimitMB
return|;
block|}
comment|/**    * @see IndexWriterConfig#setFlushPolicy(FlushPolicy)    */
DECL|method|getFlushPolicy
name|FlushPolicy
name|getFlushPolicy
parameter_list|()
block|{
return|return
name|flushPolicy
return|;
block|}
comment|/** Returns {@link InfoStream} used for debugging.    *    * @see IndexWriterConfig#setInfoStream(InfoStream)    */
DECL|method|getInfoStream
specifier|public
name|InfoStream
name|getInfoStream
parameter_list|()
block|{
return|return
name|infoStream
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"matchVersion="
argument_list|)
operator|.
name|append
argument_list|(
name|matchVersion
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"analyzer="
argument_list|)
operator|.
name|append
argument_list|(
name|analyzer
operator|==
literal|null
condition|?
literal|"null"
else|:
name|analyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ramBufferSizeMB="
argument_list|)
operator|.
name|append
argument_list|(
name|getRAMBufferSizeMB
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxBufferedDocs="
argument_list|)
operator|.
name|append
argument_list|(
name|getMaxBufferedDocs
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxBufferedDeleteTerms="
argument_list|)
operator|.
name|append
argument_list|(
name|getMaxBufferedDeleteTerms
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergedSegmentWarmer="
argument_list|)
operator|.
name|append
argument_list|(
name|getMergedSegmentWarmer
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"readerTermsIndexDivisor="
argument_list|)
operator|.
name|append
argument_list|(
name|getReaderTermsIndexDivisor
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"termIndexInterval="
argument_list|)
operator|.
name|append
argument_list|(
name|getTermIndexInterval
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
comment|// TODO: this should be private to the codec, not settable here
name|sb
operator|.
name|append
argument_list|(
literal|"delPolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|getIndexDeletionPolicy
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|IndexCommit
name|commit
init|=
name|getIndexCommit
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"commit="
argument_list|)
operator|.
name|append
argument_list|(
name|commit
operator|==
literal|null
condition|?
literal|"null"
else|:
name|commit
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"openMode="
argument_list|)
operator|.
name|append
argument_list|(
name|getOpenMode
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"similarity="
argument_list|)
operator|.
name|append
argument_list|(
name|getSimilarity
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergeScheduler="
argument_list|)
operator|.
name|append
argument_list|(
name|getMergeScheduler
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"default WRITE_LOCK_TIMEOUT="
argument_list|)
operator|.
name|append
argument_list|(
name|IndexWriterConfig
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"writeLockTimeout="
argument_list|)
operator|.
name|append
argument_list|(
name|getWriteLockTimeout
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"codec="
argument_list|)
operator|.
name|append
argument_list|(
name|getCodec
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"infoStream="
argument_list|)
operator|.
name|append
argument_list|(
name|getInfoStream
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergePolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|getMergePolicy
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"indexerThreadPool="
argument_list|)
operator|.
name|append
argument_list|(
name|getIndexerThreadPool
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"readerPooling="
argument_list|)
operator|.
name|append
argument_list|(
name|getReaderPooling
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"perThreadHardLimitMB="
argument_list|)
operator|.
name|append
argument_list|(
name|getRAMPerThreadHardLimitMB
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
