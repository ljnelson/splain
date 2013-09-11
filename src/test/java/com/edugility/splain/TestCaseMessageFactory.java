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

import java.text.ParseException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Test;

import com.edugility.objexj.Pattern;

import static org.junit.Assert.*;

public class TestCaseMessageFactory {

  public TestCaseMessageFactory() {
    super();
  }

  @Test
  public void test() throws IOException, ParseException {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("TestCaseMessageFactoryBundle/foo");
    assertNotNull(rbk);
    final MessageFactory<Character> mf = new MessageFactory<Character>();
    mf.addPattern(rbk, Pattern.<Character>compile("java.lang.Character(farg = \"blah\"; return true;)"));
    final List<Character> input = Arrays.asList('a');
    assertNotNull(input);
    assertEquals(1, input.size());
    assertEquals(Character.valueOf('a'), input.get(0));
    final String message = mf.getMessage(input, null);
    assertEquals("Hi, a, your farg is blah", message);
  }

  @Test
  public void testGlobalHandler() throws IOException, ParseException {
    final ResourceBundleKey rbk = ResourceBundleKey.valueOf("${$1[0].message}");
    final NoSuchElementException nse = new NoSuchElementException("bottom");
    final IllegalStateException ise = new IllegalStateException("top");
    final List<Throwable> l = Arrays.<Throwable>asList(ise, nse);
    final MessageFactory<Throwable> mf = new MessageFactory<Throwable>();
    mf.addPattern(rbk, Pattern.<Throwable>compile("(java.lang.Exception)$"));
    final String message = mf.getMessage(l, null);
    assertEquals("bottom", message);

  }

}
