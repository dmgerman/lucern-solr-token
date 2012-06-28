begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package
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
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|ObjectInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteOrder
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
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|AnalyzerProfile
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
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|Utility
import|;
end_import
begin_comment
comment|/**  * SmartChineseAnalyzer Word Dictionary  * @lucene.experimental  */
end_comment
begin_class
DECL|class|WordDictionary
class|class
name|WordDictionary
extends|extends
name|AbstractDictionary
block|{
DECL|method|WordDictionary
specifier|private
name|WordDictionary
parameter_list|()
block|{   }
DECL|field|singleInstance
specifier|private
specifier|static
name|WordDictionary
name|singleInstance
decl_stmt|;
comment|/**    * Large prime number for hash function    */
DECL|field|PRIME_INDEX_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|PRIME_INDEX_LENGTH
init|=
literal|12071
decl_stmt|;
comment|/**    * wordIndexTable guarantees to hash all Chinese characters in Unicode into     * PRIME_INDEX_LENGTH array. There will be conflict, but in reality this     * program only handles the 6768 characters found in GB2312 plus some     * ASCII characters. Therefore in order to guarantee better precision, it is    * necessary to retain the original symbol in the charIndexTable.    */
DECL|field|wordIndexTable
specifier|private
name|short
index|[]
name|wordIndexTable
decl_stmt|;
DECL|field|charIndexTable
specifier|private
name|char
index|[]
name|charIndexTable
decl_stmt|;
comment|/**    * To avoid taking too much space, the data structure needed to store the     * lexicon requires two multidimensional arrays to store word and frequency.    * Each word is placed in a char[]. Each char represents a Chinese char or     * other symbol.  Each frequency is put into an int. These two arrays     * correspond to each other one-to-one. Therefore, one can use     * wordItem_charArrayTable[i][j] to look up word from lexicon, and     * wordItem_frequencyTable[i][j] to look up the corresponding frequency.     */
DECL|field|wordItem_charArrayTable
specifier|private
name|char
index|[]
index|[]
index|[]
name|wordItem_charArrayTable
decl_stmt|;
DECL|field|wordItem_frequencyTable
specifier|private
name|int
index|[]
index|[]
name|wordItem_frequencyTable
decl_stmt|;
comment|// static Logger log = Logger.getLogger(WordDictionary.class);
comment|/**    * Get the singleton dictionary instance.    * @return singleton    */
DECL|method|getInstance
specifier|public
specifier|synchronized
specifier|static
name|WordDictionary
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|singleInstance
operator|==
literal|null
condition|)
block|{
name|singleInstance
operator|=
operator|new
name|WordDictionary
argument_list|()
expr_stmt|;
try|try
block|{
name|singleInstance
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|wordDictRoot
init|=
name|AnalyzerProfile
operator|.
name|ANALYSIS_DATA_DIR
decl_stmt|;
name|singleInstance
operator|.
name|load
argument_list|(
name|wordDictRoot
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
return|return
name|singleInstance
return|;
block|}
comment|/**    * Attempt to load dictionary from provided directory, first trying coredict.mem, failing back on coredict.dct    *     * @param dctFileRoot path to dictionary directory    */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|String
name|dctFileRoot
parameter_list|)
block|{
name|String
name|dctFilePath
init|=
name|dctFileRoot
operator|+
literal|"/coredict.dct"
decl_stmt|;
name|File
name|serialObj
init|=
operator|new
name|File
argument_list|(
name|dctFileRoot
operator|+
literal|"/coredict.mem"
argument_list|)
decl_stmt|;
if|if
condition|(
name|serialObj
operator|.
name|exists
argument_list|()
operator|&&
name|loadFromObj
argument_list|(
name|serialObj
argument_list|)
condition|)
block|{      }
else|else
block|{
try|try
block|{
name|wordIndexTable
operator|=
operator|new
name|short
index|[
name|PRIME_INDEX_LENGTH
index|]
expr_stmt|;
name|charIndexTable
operator|=
operator|new
name|char
index|[
name|PRIME_INDEX_LENGTH
index|]
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
name|PRIME_INDEX_LENGTH
condition|;
name|i
operator|++
control|)
block|{
name|charIndexTable
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|wordIndexTable
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|wordItem_charArrayTable
operator|=
operator|new
name|char
index|[
name|GB2312_CHAR_NUM
index|]
index|[]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
operator|=
operator|new
name|int
index|[
name|GB2312_CHAR_NUM
index|]
index|[]
expr_stmt|;
comment|// int total =
name|loadMainDataFromFile
argument_list|(
name|dctFilePath
argument_list|)
expr_stmt|;
name|expandDelimiterData
argument_list|()
expr_stmt|;
name|mergeSameWords
argument_list|()
expr_stmt|;
name|sortEachItems
argument_list|()
expr_stmt|;
comment|// log.info("load dictionary: " + dctFilePath + " total:" + total);
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|saveToObj
argument_list|(
name|serialObj
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Load coredict.mem internally from the jar file.    *     * @throws ClassNotFoundException    * @throws IOException    */
DECL|method|load
specifier|public
name|void
name|load
parameter_list|()
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|InputStream
name|input
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"coredict.mem"
argument_list|)
decl_stmt|;
name|loadFromObjectInputStream
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|loadFromObj
specifier|private
name|boolean
name|loadFromObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
try|try
block|{
name|loadFromObjectInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
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
name|IOException
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
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|loadFromObjectInputStream
specifier|private
name|void
name|loadFromObjectInputStream
parameter_list|(
name|InputStream
name|serialObjectInputStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ObjectInputStream
name|input
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|serialObjectInputStream
argument_list|)
decl_stmt|;
name|wordIndexTable
operator|=
operator|(
name|short
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|charIndexTable
operator|=
operator|(
name|char
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|wordItem_charArrayTable
operator|=
operator|(
name|char
index|[]
index|[]
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|wordItem_frequencyTable
operator|=
operator|(
name|int
index|[]
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
comment|// log.info("load core dict from serialization.");
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|saveToObj
specifier|private
name|void
name|saveToObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
try|try
block|{
name|ObjectOutputStream
name|output
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordIndexTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|charIndexTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordItem_charArrayTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|wordItem_frequencyTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// log.info("serialize core dict.");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// log.warn(e.getMessage());
block|}
block|}
comment|/**    * Load the datafile into this WordDictionary    *     * @param dctFilePath path to word dictionary (coredict.dct)    * @return number of words read    * @throws FileNotFoundException    * @throws IOException    * @throws UnsupportedEncodingException    */
DECL|method|loadMainDataFromFile
specifier|private
name|int
name|loadMainDataFromFile
parameter_list|(
name|String
name|dctFilePath
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
decl_stmt|,
name|cnt
decl_stmt|,
name|length
decl_stmt|,
name|total
init|=
literal|0
decl_stmt|;
comment|// The file only counted 6763 Chinese characters plus 5 reserved slots 3756~3760.
comment|// The 3756th is used (as a header) to store information.
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
name|byte
index|[]
name|intBuffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|String
name|tmpword
decl_stmt|;
name|RandomAccessFile
name|dctFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|dctFilePath
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
comment|// GB2312 characters 0 - 6768
for|for
control|(
name|i
operator|=
name|GB2312_FIRST_CHAR
init|;
name|i
operator|<
name|GB2312_FIRST_CHAR
operator|+
name|CHAR_NUM_IN_FILE
condition|;
name|i
operator|++
control|)
block|{
comment|// if (i == 5231)
comment|// System.out.println(i);
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
comment|// the dictionary was developed for C, and byte order must be converted to work with Java
name|cnt
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|cnt
operator|<=
literal|0
condition|)
block|{
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
operator|new
name|char
index|[
name|cnt
index|]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|cnt
index|]
expr_stmt|;
name|total
operator|+=
name|cnt
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|cnt
condition|)
block|{
comment|// wordItemTable[i][j] = new WordItem();
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// frequency
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// length
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|2
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// handle
comment|// wordItemTable[i][j].frequency = buffer[0];
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|buffer
index|[
literal|0
index|]
expr_stmt|;
name|length
operator|=
name|buffer
index|[
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|lchBuffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|dctFile
operator|.
name|read
argument_list|(
name|lchBuffer
argument_list|)
expr_stmt|;
name|tmpword
operator|=
operator|new
name|String
argument_list|(
name|lchBuffer
argument_list|,
literal|"GB2312"
argument_list|)
expr_stmt|;
comment|// indexTable[i].wordItems[j].word = tmpword;
comment|// wordItemTable[i][j].charArray = tmpword.toCharArray();
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|tmpword
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// wordItemTable[i][j].charArray = null;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|// System.out.println(indexTable[i].wordItems[j]);
name|j
operator|++
expr_stmt|;
block|}
name|String
name|str
init|=
name|getCCByGB2312Id
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|setTableIndex
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|dctFile
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|total
return|;
block|}
comment|/**    * The original lexicon puts all information with punctuation into a     * chart (from 1 to 3755). Here it then gets expanded, separately being    * placed into the chart that has the corresponding symbol.    */
DECL|method|expandDelimiterData
specifier|private
name|void
name|expandDelimiterData
parameter_list|()
block|{
name|int
name|i
decl_stmt|;
name|int
name|cnt
decl_stmt|;
comment|// Punctuation then treating index 3755 as 1,
comment|// distribute the original punctuation corresponding dictionary into
name|int
name|delimiterIndex
init|=
literal|3755
operator|+
name|GB2312_FIRST_CHAR
decl_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|.
name|length
condition|)
block|{
name|char
name|c
init|=
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
index|[
literal|0
index|]
decl_stmt|;
name|int
name|j
init|=
name|getGB2312Id
argument_list|(
name|c
argument_list|)
decl_stmt|;
comment|// the id value of the punctuation
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|j
index|]
operator|==
literal|null
condition|)
block|{
name|int
name|k
init|=
name|i
decl_stmt|;
comment|// Starting from i, count the number of the following worditem symbol from j
while|while
condition|(
name|k
operator|<
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|.
name|length
operator|&&
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|k
index|]
index|[
literal|0
index|]
operator|==
name|c
condition|)
block|{
name|k
operator|++
expr_stmt|;
block|}
comment|// c is the punctuation character, j is the id value of c
comment|// k-1 represents the index of the last punctuation character
name|cnt
operator|=
name|k
operator|-
name|i
expr_stmt|;
if|if
condition|(
name|cnt
operator|!=
literal|0
condition|)
block|{
name|wordItem_charArrayTable
index|[
name|j
index|]
operator|=
operator|new
name|char
index|[
name|cnt
index|]
index|[]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|j
index|]
operator|=
operator|new
name|int
index|[
name|cnt
index|]
expr_stmt|;
block|}
comment|// Assign value for each wordItem.
for|for
control|(
name|k
operator|=
literal|0
init|;
name|k
operator|<
name|cnt
condition|;
name|k
operator|++
operator|,
name|i
operator|++
control|)
block|{
comment|// wordItemTable[j][k] = new WordItem();
name|wordItem_frequencyTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|=
operator|new
name|char
index|[
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
index|[
name|i
index|]
argument_list|,
literal|1
argument_list|,
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|j
index|]
index|[
name|k
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|setTableIndex
argument_list|(
name|c
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Delete the original corresponding symbol array.
name|wordItem_charArrayTable
index|[
name|delimiterIndex
index|]
operator|=
literal|null
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|delimiterIndex
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|/*    * since we aren't doing POS-tagging, merge the frequencies for entries of the same word (with different POS)    */
DECL|method|mergeSameWords
specifier|private
name|void
name|mergeSameWords
parameter_list|()
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|GB2312_FIRST_CHAR
operator|+
name|CHAR_NUM_IN_FILE
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|==
literal|null
condition|)
continue|continue;
name|int
name|len
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
name|len
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|)
block|{
name|char
index|[]
index|[]
name|tempArray
init|=
operator|new
name|char
index|[
name|len
index|]
index|[]
decl_stmt|;
name|int
index|[]
name|tempFreq
init|=
operator|new
name|int
index|[
name|len
index|]
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
name|tempArray
index|[
literal|0
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
literal|0
index|]
expr_stmt|;
name|tempFreq
index|[
literal|0
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
literal|0
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|tempArray
index|[
name|k
index|]
argument_list|,
literal|0
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|k
operator|++
expr_stmt|;
comment|// temp[k] = wordItemTable[i][j];
name|tempArray
index|[
name|k
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|tempFreq
index|[
name|k
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// temp[k].frequency += wordItemTable[i][j].frequency;
name|tempFreq
index|[
name|k
index|]
operator|+=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
comment|// wordItemTable[i] = temp;
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|=
name|tempArray
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
operator|=
name|tempFreq
expr_stmt|;
block|}
block|}
block|}
DECL|method|sortEachItems
specifier|private
name|void
name|sortEachItems
parameter_list|()
block|{
name|char
index|[]
name|tmpArray
decl_stmt|;
name|int
name|tmpFreq
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
name|wordItem_charArrayTable
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
operator|-
literal|1
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|j2
init|=
name|j
operator|+
literal|1
init|;
name|j2
operator|<
name|wordItem_charArrayTable
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j2
operator|++
control|)
block|{
if|if
condition|(
name|Utility
operator|.
name|compareArray
argument_list|(
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
condition|)
block|{
name|tmpArray
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|tmpFreq
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j2
index|]
expr_stmt|;
name|wordItem_charArrayTable
index|[
name|i
index|]
index|[
name|j2
index|]
operator|=
name|tmpArray
expr_stmt|;
name|wordItem_frequencyTable
index|[
name|i
index|]
index|[
name|j2
index|]
operator|=
name|tmpFreq
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/*    * Calculate character c's position in hash table,     * then initialize the value of that position in the address table.    */
DECL|method|setTableIndex
specifier|private
name|boolean
name|setTableIndex
parameter_list|(
name|char
name|c
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|index
init|=
name|getAvaliableTableIndex
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
name|charIndexTable
index|[
name|index
index|]
operator|=
name|c
expr_stmt|;
name|wordIndexTable
index|[
name|index
index|]
operator|=
operator|(
name|short
operator|)
name|j
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
DECL|method|getAvaliableTableIndex
specifier|private
name|short
name|getAvaliableTableIndex
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hash1
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|charIndexTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|!=
name|c
operator|&&
name|i
operator|<
name|PRIME_INDEX_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_INDEX_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|// System.out.println(i - 1);
if|if
condition|(
name|i
operator|<
name|PRIME_INDEX_LENGTH
operator|&&
operator|(
name|charIndexTable
index|[
name|index
index|]
operator|==
literal|0
operator|||
name|charIndexTable
index|[
name|index
index|]
operator|==
name|c
operator|)
condition|)
block|{
return|return
operator|(
name|short
operator|)
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getWordItemTableIndex
specifier|private
name|short
name|getWordItemTableIndex
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hash1
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|c
argument_list|)
operator|%
name|PRIME_INDEX_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_INDEX_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|charIndexTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|!=
name|c
operator|&&
name|i
operator|<
name|PRIME_INDEX_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_INDEX_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<
name|PRIME_INDEX_LENGTH
operator|&&
name|charIndexTable
index|[
name|index
index|]
operator|==
name|c
condition|)
block|{
return|return
operator|(
name|short
operator|)
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Look up the text string corresponding with the word char array,     * and return the position of the word list.    *     * @param knownHashIndex already figure out position of the first word     *   symbol charArray[0] in hash table. If not calculated yet, can be     *   replaced with function int findInTable(char[] charArray).    * @param charArray look up the char array corresponding with the word.    * @return word location in word array.  If not found, then return -1.    */
DECL|method|findInTable
specifier|private
name|int
name|findInTable
parameter_list|(
name|short
name|knownHashIndex
parameter_list|,
name|char
index|[]
name|charArray
parameter_list|)
block|{
if|if
condition|(
name|charArray
operator|==
literal|null
operator|||
name|charArray
operator|.
name|length
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|char
index|[]
index|[]
name|items
init|=
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|knownHashIndex
index|]
index|]
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|,
name|end
init|=
name|items
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
decl_stmt|,
name|cmpResult
decl_stmt|;
comment|// Binary search for the index of idArray
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|cmpResult
operator|=
name|Utility
operator|.
name|compareArray
argument_list|(
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|,
name|charArray
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmpResult
operator|==
literal|0
condition|)
return|return
name|mid
return|;
comment|// find it
elseif|else
if|if
condition|(
name|cmpResult
operator|<
literal|0
condition|)
name|start
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmpResult
operator|>
literal|0
condition|)
name|end
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
name|mid
operator|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Find the first word in the dictionary that starts with the supplied prefix    *     * @see #getPrefixMatch(char[], int)    * @param charArray input prefix    * @return index of word, or -1 if not found    */
DECL|method|getPrefixMatch
specifier|public
name|int
name|getPrefixMatch
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
return|return
name|getPrefixMatch
argument_list|(
name|charArray
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Find the nth word in the dictionary that starts with the supplied prefix    *     * @see #getPrefixMatch(char[])    * @param charArray input prefix    * @param knownStart relative position in the dictionary to start    * @return index of word, or -1 if not found    */
DECL|method|getPrefixMatch
specifier|public
name|int
name|getPrefixMatch
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|,
name|int
name|knownStart
parameter_list|)
block|{
name|short
name|index
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
return|return
operator|-
literal|1
return|;
name|char
index|[]
index|[]
name|items
init|=
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|index
index|]
index|]
decl_stmt|;
name|int
name|start
init|=
name|knownStart
decl_stmt|,
name|end
init|=
name|items
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
decl_stmt|,
name|cmpResult
decl_stmt|;
comment|// Binary search for the index of idArray
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|cmpResult
operator|=
name|Utility
operator|.
name|compareArrayByPrefix
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmpResult
operator|==
literal|0
condition|)
block|{
comment|// Get the first item which match the current word
while|while
condition|(
name|mid
operator|>=
literal|0
operator|&&
name|Utility
operator|.
name|compareArrayByPrefix
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|items
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
condition|)
name|mid
operator|--
expr_stmt|;
name|mid
operator|++
expr_stmt|;
return|return
name|mid
return|;
comment|// Find the first word that uses charArray as prefix.
block|}
elseif|else
if|if
condition|(
name|cmpResult
operator|<
literal|0
condition|)
name|end
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
name|start
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
name|mid
operator|=
operator|(
name|start
operator|+
name|end
operator|)
operator|/
literal|2
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Get the frequency of a word from the dictionary    *     * @param charArray input word    * @return word frequency, or zero if the word is not found    */
DECL|method|getFrequency
specifier|public
name|int
name|getFrequency
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|)
block|{
name|short
name|hashIndex
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|hashIndex
operator|==
operator|-
literal|1
condition|)
return|return
literal|0
return|;
name|int
name|itemIndex
init|=
name|findInTable
argument_list|(
name|hashIndex
argument_list|,
name|charArray
argument_list|)
decl_stmt|;
if|if
condition|(
name|itemIndex
operator|!=
operator|-
literal|1
condition|)
return|return
name|wordItem_frequencyTable
index|[
name|wordIndexTable
index|[
name|hashIndex
index|]
index|]
index|[
name|itemIndex
index|]
return|;
return|return
literal|0
return|;
block|}
comment|/**    * Return true if the dictionary entry at itemIndex for table charArray[0] is charArray    *     * @param charArray input word    * @param itemIndex item index for table charArray[0]    * @return true if the entry exists    */
DECL|method|isEqual
specifier|public
name|boolean
name|isEqual
parameter_list|(
name|char
index|[]
name|charArray
parameter_list|,
name|int
name|itemIndex
parameter_list|)
block|{
name|short
name|hashIndex
init|=
name|getWordItemTableIndex
argument_list|(
name|charArray
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|Utility
operator|.
name|compareArray
argument_list|(
name|charArray
argument_list|,
literal|1
argument_list|,
name|wordItem_charArrayTable
index|[
name|wordIndexTable
index|[
name|hashIndex
index|]
index|]
index|[
name|itemIndex
index|]
argument_list|,
literal|0
argument_list|)
operator|==
literal|0
return|;
block|}
block|}
end_class
end_unit
