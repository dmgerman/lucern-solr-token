begin_unit
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *<p>Fast, well distributed, cross-platform hash functions.  *</p>  *  *<p>Development background: I was surprised to discovered that there isn't a good cross-platform hash function defined for strings. MD5, SHA, FVN, etc, all define hash functions over bytes, meaning that it's under-specified for strings.  *</p>  *  *<p>So I set out to create a standard 32 bit string hash that would be well defined for implementation in all languages, have very high performance, and have very good hash properties such as distribution. After evaluating all the options, I settled on using Bob Jenkins' lookup3 as a base. It's a well studied and very fast hash function, and the hashword variant can work with 32 bits at a time (perfect for hashing unicode code points). It's also even faster on the latest JVMs which can translate pairs of shifts into native rotate instructions.  *</p>  *<p>The only problem with using lookup3 hashword is that it includes a length in the initial value. This would suck some performance out since directly hashing a UTF8 or UTF16 string (Java) would require a pre-scan to get the actual number of unicode code points. The solution was to simply remove the length factor, which is equivalent to biasing initVal by -(numCodePoints*4). This slightly modified lookup3 I define as lookup3ycs.  *</p>  *<p>So the definition of the cross-platform string hash lookup3ycs is as follows:  *</p>  *<p>The hash value of a character sequence (a string) is defined to be the hash of its unicode code points, according to lookup3 hashword, with the initval biased by -(length*4).  *</p>  *<p>So by definition  *</p>  *<pre>  * lookup3ycs(k,offset,length,initval) == lookup3(k,offset,length,initval-(length*4))  *  * AND  *  * lookup3ycs(k,offset,length,initval+(length*4)) == lookup3(k,offset,length,initval)  *</pre>  *<p>An obvious advantage of this relationship is that you can use lookup3 if you don't have an implementation of lookup3ycs.  *</p>  */
end_comment
begin_class
DECL|class|Hash
specifier|public
class|class
name|Hash
block|{
comment|/**    * A Java implementation of hashword from lookup3.c by Bob Jenkins    * (<a href="http://burtleburtle.net/bob/c/lookup3.c">original source</a>).    *    * @param k   the key to hash    * @param offset   offset of the start of the key    * @param length   length of the key    * @param initval  initial value to fold into the hash    * @return  the 32 bit hash code    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|lookup3
specifier|public
specifier|static
name|int
name|lookup3
parameter_list|(
name|int
index|[]
name|k
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|initval
parameter_list|)
block|{
name|int
name|a
decl_stmt|,
name|b
decl_stmt|,
name|c
decl_stmt|;
name|a
operator|=
name|b
operator|=
name|c
operator|=
literal|0xdeadbeef
operator|+
operator|(
name|length
operator|<<
literal|2
operator|)
operator|+
name|initval
expr_stmt|;
name|int
name|i
init|=
name|offset
decl_stmt|;
while|while
condition|(
name|length
operator|>
literal|3
condition|)
block|{
name|a
operator|+=
name|k
index|[
name|i
index|]
expr_stmt|;
name|b
operator|+=
name|k
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|c
operator|+=
name|k
index|[
name|i
operator|+
literal|2
index|]
expr_stmt|;
comment|// mix(a,b,c)... Java needs "out" parameters!!!
comment|// Note: recent JVMs (Sun JDK6) turn pairs of shifts (needed to do a rotate)
comment|// into real x86 rotate instructions.
block|{
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|6
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|6
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|8
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|8
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|16
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|19
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|19
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|4
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
block|}
name|length
operator|-=
literal|3
expr_stmt|;
name|i
operator|+=
literal|3
expr_stmt|;
block|}
switch|switch
condition|(
name|length
condition|)
block|{
case|case
literal|3
case|:
name|c
operator|+=
name|k
index|[
name|i
operator|+
literal|2
index|]
expr_stmt|;
comment|// fall through
case|case
literal|2
case|:
name|b
operator|+=
name|k
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
comment|// fall through
case|case
literal|1
case|:
name|a
operator|+=
name|k
index|[
name|i
operator|+
literal|0
index|]
expr_stmt|;
comment|// fall through
comment|// final(a,b,c);
block|{
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|14
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|11
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|11
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|25
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|25
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|16
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|14
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|24
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|24
operator|)
expr_stmt|;
block|}
case|case
literal|0
case|:
break|break;
block|}
return|return
name|c
return|;
block|}
comment|/**    * Identical to lookup3, except initval is biased by -(length&lt;&lt;2).    * This is equivalent to leaving out the length factor in the initial state.    * {@code lookup3ycs(k,offset,length,initval) == lookup3(k,offset,length,initval-(length<<2))}    * and    * {@code lookup3ycs(k,offset,length,initval+(length<<2)) == lookup3(k,offset,length,initval)}    */
DECL|method|lookup3ycs
specifier|public
specifier|static
name|int
name|lookup3ycs
parameter_list|(
name|int
index|[]
name|k
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|initval
parameter_list|)
block|{
return|return
name|lookup3
argument_list|(
name|k
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|initval
operator|-
operator|(
name|length
operator|<<
literal|2
operator|)
argument_list|)
return|;
block|}
comment|/**    *<p>The hash value of a character sequence is defined to be the hash of    * its unicode code points, according to {@link #lookup3ycs(int[] k, int offset, int length, int initval)}    *</p>    *<p>If you know the number of code points in the {@code CharSequence}, you can    * generate the same hash as the original lookup3    * via {@code lookup3ycs(s, start, end, initval+(numCodePoints<<2))}    */
DECL|method|lookup3ycs
specifier|public
specifier|static
name|int
name|lookup3ycs
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|initval
parameter_list|)
block|{
name|int
name|a
decl_stmt|,
name|b
decl_stmt|,
name|c
decl_stmt|;
name|a
operator|=
name|b
operator|=
name|c
operator|=
literal|0xdeadbeef
operator|+
name|initval
expr_stmt|;
comment|// only difference from lookup3 is that "+ (length<<2)" is missing
comment|// since we don't know the number of code points to start with,
comment|// and don't want to have to pre-scan the string to find out.
name|int
name|i
init|=
name|start
decl_stmt|;
name|boolean
name|mixed
init|=
literal|true
decl_stmt|;
comment|// have the 3 state variables been adequately mixed?
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|mixed
operator|=
literal|false
expr_stmt|;
name|char
name|ch
decl_stmt|;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|a
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|b
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|c
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
comment|// mix(a,b,c)... Java needs "out" parameters!!!
comment|// Note: recent JVMs (Sun JDK6) turn pairs of shifts (needed to do a rotate)
comment|// into real x86 rotate instructions.
block|{
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|6
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|6
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|8
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|8
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|16
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|19
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|19
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|4
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
block|}
name|mixed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mixed
condition|)
block|{
comment|// final(a,b,c)
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|14
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|11
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|11
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|25
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|25
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|16
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|14
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|24
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|24
operator|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
comment|/**<p>This is the 64 bit version of lookup3ycs, corresponding to Bob Jenkin's    * lookup3 hashlittle2 with initval biased by -(numCodePoints&lt;&lt;2).  It is equivalent    * to lookup3ycs in that if the high bits of initval==0, then the low bits of the    * result will be the same as lookup3ycs.    *</p>    */
DECL|method|lookup3ycs64
specifier|public
specifier|static
name|long
name|lookup3ycs64
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|long
name|initval
parameter_list|)
block|{
name|int
name|a
decl_stmt|,
name|b
decl_stmt|,
name|c
decl_stmt|;
name|a
operator|=
name|b
operator|=
name|c
operator|=
literal|0xdeadbeef
operator|+
operator|(
name|int
operator|)
name|initval
expr_stmt|;
name|c
operator|+=
call|(
name|int
call|)
argument_list|(
name|initval
operator|>>>
literal|32
argument_list|)
expr_stmt|;
comment|// only difference from lookup3 is that "+ (length<<2)" is missing
comment|// since we don't know the number of code points to start with,
comment|// and don't want to have to pre-scan the string to find out.
name|int
name|i
init|=
name|start
decl_stmt|;
name|boolean
name|mixed
init|=
literal|true
decl_stmt|;
comment|// have the 3 state variables been adequately mixed?
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|mixed
operator|=
literal|false
expr_stmt|;
name|char
name|ch
decl_stmt|;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|a
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|b
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
name|c
operator|+=
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|ch
argument_list|)
operator|&&
name|i
operator|<
name|end
condition|?
name|Character
operator|.
name|toCodePoint
argument_list|(
name|ch
argument_list|,
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
else|:
name|ch
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|end
condition|)
break|break;
comment|// mix(a,b,c)... Java needs "out" parameters!!!
comment|// Note: recent JVMs (Sun JDK6) turn pairs of shifts (needed to do a rotate)
comment|// into real x86 rotate instructions.
block|{
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|6
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|6
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|8
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|8
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
name|a
operator|-=
name|c
expr_stmt|;
name|a
operator|^=
operator|(
name|c
operator|<<
literal|16
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|c
operator|+=
name|b
expr_stmt|;
name|b
operator|-=
name|a
expr_stmt|;
name|b
operator|^=
operator|(
name|a
operator|<<
literal|19
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|19
operator|)
expr_stmt|;
name|a
operator|+=
name|c
expr_stmt|;
name|c
operator|-=
name|b
expr_stmt|;
name|c
operator|^=
operator|(
name|b
operator|<<
literal|4
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|+=
name|a
expr_stmt|;
block|}
name|mixed
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mixed
condition|)
block|{
comment|// final(a,b,c)
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|14
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|11
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|11
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|25
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|25
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|16
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|16
operator|)
expr_stmt|;
name|a
operator|^=
name|c
expr_stmt|;
name|a
operator|-=
operator|(
name|c
operator|<<
literal|4
operator|)
operator||
operator|(
name|c
operator|>>>
operator|-
literal|4
operator|)
expr_stmt|;
name|b
operator|^=
name|a
expr_stmt|;
name|b
operator|-=
operator|(
name|a
operator|<<
literal|14
operator|)
operator||
operator|(
name|a
operator|>>>
operator|-
literal|14
operator|)
expr_stmt|;
name|c
operator|^=
name|b
expr_stmt|;
name|c
operator|-=
operator|(
name|b
operator|<<
literal|24
operator|)
operator||
operator|(
name|b
operator|>>>
operator|-
literal|24
operator|)
expr_stmt|;
block|}
return|return
name|c
operator|+
operator|(
operator|(
operator|(
name|long
operator|)
name|b
operator|)
operator|<<
literal|32
operator|)
return|;
block|}
comment|/** Returns the MurmurHash3_x86_32 hash.    * Original source/tests at https://github.com/yonik/java_util/    */
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
specifier|final
name|int
name|c1
init|=
literal|0xcc9e2d51
decl_stmt|;
specifier|final
name|int
name|c2
init|=
literal|0x1b873593
decl_stmt|;
name|int
name|h1
init|=
name|seed
decl_stmt|;
name|int
name|roundedEnd
init|=
name|offset
operator|+
operator|(
name|len
operator|&
literal|0xfffffffc
operator|)
decl_stmt|;
comment|// round down to 4 byte block
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|roundedEnd
condition|;
name|i
operator|+=
literal|4
control|)
block|{
comment|// little endian load order
name|int
name|k1
init|=
operator|(
name|data
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|data
index|[
name|i
operator|+
literal|3
index|]
operator|<<
literal|24
operator|)
decl_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
operator|(
name|k1
operator|<<
literal|15
operator|)
operator||
operator|(
name|k1
operator|>>>
literal|17
operator|)
expr_stmt|;
comment|// ROTL32(k1,15);
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
name|h1
operator|=
operator|(
name|h1
operator|<<
literal|13
operator|)
operator||
operator|(
name|h1
operator|>>>
literal|19
operator|)
expr_stmt|;
comment|// ROTL32(h1,13);
name|h1
operator|=
name|h1
operator|*
literal|5
operator|+
literal|0xe6546b64
expr_stmt|;
block|}
comment|// tail
name|int
name|k1
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|len
operator|&
literal|0x03
condition|)
block|{
case|case
literal|3
case|:
name|k1
operator|=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
expr_stmt|;
comment|// fallthrough
case|case
literal|2
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
expr_stmt|;
comment|// fallthrough
case|case
literal|1
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
operator|(
name|k1
operator|<<
literal|15
operator|)
operator||
operator|(
name|k1
operator|>>>
literal|17
operator|)
expr_stmt|;
comment|// ROTL32(k1,15);
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
block|}
comment|// finalization
name|h1
operator|^=
name|len
expr_stmt|;
comment|// fmix(h1);
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
name|h1
operator|*=
literal|0x85ebca6b
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|13
expr_stmt|;
name|h1
operator|*=
literal|0xc2b2ae35
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
return|return
name|h1
return|;
block|}
comment|/** Returns the MurmurHash3_x86_32 hash of the UTF-8 bytes of the String without actually encoding    * the string to a temporary buffer.  This is more than 2x faster than hashing the result    * of String.getBytes().    */
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|CharSequence
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
specifier|final
name|int
name|c1
init|=
literal|0xcc9e2d51
decl_stmt|;
specifier|final
name|int
name|c2
init|=
literal|0x1b873593
decl_stmt|;
name|int
name|h1
init|=
name|seed
decl_stmt|;
name|int
name|pos
init|=
name|offset
decl_stmt|;
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
name|int
name|k1
init|=
literal|0
decl_stmt|;
name|int
name|k2
init|=
literal|0
decl_stmt|;
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|int
name|bits
init|=
literal|0
decl_stmt|;
name|int
name|nBytes
init|=
literal|0
decl_stmt|;
comment|// length in UTF8 bytes
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|int
name|code
init|=
name|data
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|<
literal|0x80
condition|)
block|{
name|k2
operator|=
name|code
expr_stmt|;
name|bits
operator|=
literal|8
expr_stmt|;
comment|/***          // optimized ascii implementation (currently slower!!! code size?)          if (shift == 24) {          k1 = k1 | (code<< 24);           k1 *= c1;          k1 = (k1<< 15) | (k1>>> 17);  // ROTL32(k1,15);          k1 *= c2;           h1 ^= k1;          h1 = (h1<< 13) | (h1>>> 19);  // ROTL32(h1,13);          h1 = h1*5+0xe6546b64;           shift = 0;          nBytes += 4;          k1 = 0;          } else {          k1 |= code<< shift;          shift += 8;          }          continue;          ***/
block|}
elseif|else
if|if
condition|(
name|code
operator|<
literal|0x800
condition|)
block|{
name|k2
operator|=
operator|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
operator|)
operator||
operator|(
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
operator|<<
literal|8
operator|)
expr_stmt|;
name|bits
operator|=
literal|16
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
argument_list|<
literal|0xD800
operator|||
name|code
argument_list|>
literal|0xDFFF
operator|||
name|pos
operator|>=
name|end
condition|)
block|{
comment|// we check for pos>=end to encode an unpaired surrogate as 3 bytes.
name|k2
operator|=
operator|(
literal|0xE0
operator||
operator|(
name|code
operator|>>
literal|12
operator|)
operator|)
operator||
operator|(
operator|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
operator|<<
literal|16
operator|)
expr_stmt|;
name|bits
operator|=
literal|24
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate pair
comment|// int utf32 = pos< end ? (int) data.charAt(pos++) : 0;
name|int
name|utf32
init|=
operator|(
name|int
operator|)
name|data
operator|.
name|charAt
argument_list|(
name|pos
operator|++
argument_list|)
decl_stmt|;
name|utf32
operator|=
operator|(
operator|(
name|code
operator|-
literal|0xD7C0
operator|)
operator|<<
literal|10
operator|)
operator|+
operator|(
name|utf32
operator|&
literal|0x3FF
operator|)
expr_stmt|;
name|k2
operator|=
operator|(
literal|0xff
operator|&
operator|(
literal|0xF0
operator||
operator|(
name|utf32
operator|>>
literal|18
operator|)
operator|)
operator|)
operator||
operator|(
operator|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|12
operator|)
operator|&
literal|0x3F
operator|)
operator|)
operator|)
operator|<<
literal|8
operator||
operator|(
operator|(
literal|0x80
operator||
operator|(
operator|(
name|utf32
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
operator|)
operator|)
operator|<<
literal|16
operator||
operator|(
literal|0x80
operator||
operator|(
name|utf32
operator|&
literal|0x3F
operator|)
operator|)
operator|<<
literal|24
expr_stmt|;
name|bits
operator|=
literal|32
expr_stmt|;
block|}
name|k1
operator||=
name|k2
operator|<<
name|shift
expr_stmt|;
comment|// int used_bits = 32 - shift;  // how many bits of k2 were used in k1.
comment|// int unused_bits = bits - used_bits; //  (bits-(32-shift)) == bits+shift-32  == bits-newshift
name|shift
operator|+=
name|bits
expr_stmt|;
if|if
condition|(
name|shift
operator|>=
literal|32
condition|)
block|{
comment|// mix after we have a complete word
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
operator|(
name|k1
operator|<<
literal|15
operator|)
operator||
operator|(
name|k1
operator|>>>
literal|17
operator|)
expr_stmt|;
comment|// ROTL32(k1,15);
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
name|h1
operator|=
operator|(
name|h1
operator|<<
literal|13
operator|)
operator||
operator|(
name|h1
operator|>>>
literal|19
operator|)
expr_stmt|;
comment|// ROTL32(h1,13);
name|h1
operator|=
name|h1
operator|*
literal|5
operator|+
literal|0xe6546b64
expr_stmt|;
name|shift
operator|-=
literal|32
expr_stmt|;
comment|// unfortunately, java won't let you shift 32 bits off, so we need to check for 0
if|if
condition|(
name|shift
operator|!=
literal|0
condition|)
block|{
name|k1
operator|=
name|k2
operator|>>>
operator|(
name|bits
operator|-
name|shift
operator|)
expr_stmt|;
comment|// bits used == bits - newshift
block|}
else|else
block|{
name|k1
operator|=
literal|0
expr_stmt|;
block|}
name|nBytes
operator|+=
literal|4
expr_stmt|;
block|}
block|}
comment|// inner
comment|// handle tail
if|if
condition|(
name|shift
operator|>
literal|0
condition|)
block|{
name|nBytes
operator|+=
name|shift
operator|>>
literal|3
expr_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
operator|(
name|k1
operator|<<
literal|15
operator|)
operator||
operator|(
name|k1
operator|>>>
literal|17
operator|)
expr_stmt|;
comment|// ROTL32(k1,15);
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
block|}
comment|// finalization
name|h1
operator|^=
name|nBytes
expr_stmt|;
comment|// fmix(h1);
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
name|h1
operator|*=
literal|0x85ebca6b
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|13
expr_stmt|;
name|h1
operator|*=
literal|0xc2b2ae35
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
return|return
name|h1
return|;
block|}
block|}
end_class
end_unit
