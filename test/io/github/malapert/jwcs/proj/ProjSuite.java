/* 
 * Copyright (C) 2014 Jean-Christophe Malapert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.malapert.jwcs.proj;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author malapert
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    io.github.malapert.jwcs.proj.AITTest.class, 
    io.github.malapert.jwcs.proj.ARCTest.class, 
    io.github.malapert.jwcs.proj.AZPTest.class,
    io.github.malapert.jwcs.proj.BONTest.class,  
    io.github.malapert.jwcs.proj.CARTest.class,
    io.github.malapert.jwcs.proj.CEATest.class,
    io.github.malapert.jwcs.proj.CODTest.class, 
    io.github.malapert.jwcs.proj.COETest.class, 
    io.github.malapert.jwcs.proj.COOTest.class, 
    io.github.malapert.jwcs.proj.COPTest.class,
    io.github.malapert.jwcs.proj.CYPTest.class,
    io.github.malapert.jwcs.proj.PCOTest.class,
    io.github.malapert.jwcs.proj.SFLTest.class,    
    io.github.malapert.jwcs.proj.SINTest.class,
    io.github.malapert.jwcs.proj.STGTest.class, 
    io.github.malapert.jwcs.proj.SZPTest.class, 
    io.github.malapert.jwcs.proj.TANTest.class,     
    io.github.malapert.jwcs.proj.MERTest.class,
    io.github.malapert.jwcs.proj.MOLTest.class,
    io.github.malapert.jwcs.proj.PARTest.class,    
    io.github.malapert.jwcs.proj.ZEATest.class, 
    io.github.malapert.jwcs.proj.ZPNTest.class})

public class ProjSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
