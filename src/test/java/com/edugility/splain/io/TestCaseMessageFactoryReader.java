/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright (c) 2013 Edugility LLC.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package com.edugility.splain.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.sql.SQLException;

import java.text.ParseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.junit.Test;

import com.edugility.objexj.Pattern;

import com.edugility.splain.MessageFactory;

import static org.junit.Assert.*;

public class TestCaseMessageFactoryReader {

  public static final String LS = System.getProperty("line.separator", "\n");

  public TestCaseMessageFactoryReader() {
    super();
  }

  public void testPrecedence() throws IOException, ParseException {
    final InputStreamReader isr = new InputStreamReader(this.getClass().getResource("/MessageCatalog.mc").openStream());
    final MessageFactoryReader r = new MessageFactoryReader(isr);
    final MessageFactory<Object> mf = r.read();
    assertNotNull(mf);
    isr.close();    
    
    final SQLException sqlException = new SQLException("Bottom");
    final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Top", sqlException);

    List<Object> input = Arrays.<Object>asList(illegalArgumentException, sqlException);
    assertNotNull(input);
    
    String message = mf.getMessage(input);
    assertNotNull(message);
    assertEquals("There was a database exception.", message);

    final IOException io = new IOException("boom");
    input = Arrays.<Object>asList(sqlException, io);
    assertNotNull(input);

    message = mf.getMessage(input);
    assertNotNull(message);
    assertEquals("An unknown error occurred", message);

  }

  @Test
  public void test() throws IOException, ParseException {
    final String rbSource = String.format("foo = Hi, @{$0}%n");    
    assertNotNull(rbSource);
    final StringReader reader = new StringReader(rbSource);
    final PropertyResourceBundle rb = new PropertyResourceBundle(reader);
    reader.close();

    final StringBuilder source = new StringBuilder("java.lang.Exception(message == \"fred\")");
    source.append(LS);
    source.append("--").append(LS);
    source.append("/foo").append(LS);
    source.append(LS);
    

    final StringReader sr = new StringReader(source.toString());
    final MessageFactoryReader r = new MessageFactoryReader(sr, rb);
    final MessageFactory<Exception> mf = r.read();
    assertNotNull(mf);
    final List<Exception> input = Arrays.asList(new Exception("fred"));
    assertNotNull(input);
    assertEquals(1, input.size());
    final Object o = mf.getMessage(input);
    assertEquals("Hi, [java.lang.Exception: fred]", o);
    
  }

}
