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
package com.edugility.splain;

import java.io.IOException;
import java.io.StringReader;

import java.text.ParseException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.junit.BeforeClass;
import org.junit.Test;

import com.edugility.objexj.Pattern;

import static org.junit.Assert.*;

public class TestCaseMessageFactory {

  private static ResourceBundle rb;

  public TestCaseMessageFactory() {
    super();
  }

  @BeforeClass
  public static void setUp() throws IOException {
    final String rbSource = String.format("foo = Hi, @{$0[0]}, your farg is @{farg}  %n");
    assertNotNull(rbSource);
    final StringReader reader = new StringReader(rbSource);
    rb = new PropertyResourceBundle(reader);
    reader.close();
  }

  @Test
  public void test() throws IOException, ParseException {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf(rb, "/foo");
    assertNotNull(rbk);

    final MessageFactory<Character> mf = new MessageFactory<Character>();
    mf.addPattern(rbk, Pattern.<Character>compile("java.lang.Character(farg = \"blah\"; return true;)"));
    final List<Character> input = Arrays.asList('a');
    assertNotNull(input);
    assertEquals(1, input.size());
    assertEquals(Character.valueOf('a'), input.get(0));
    final String message = mf.getMessage(input);
    assertEquals("Hi, a, your farg is blah  ", message);
    
  }

}