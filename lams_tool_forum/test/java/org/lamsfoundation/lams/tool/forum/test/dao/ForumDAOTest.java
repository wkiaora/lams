/*
 *Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 *
 *This program is free software; you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *USA
 *
 *http://www.gnu.org/licenses/gpl.txt
 */
package org.lamsfoundation.lams.tool.forum.test.dao;

import org.lamsfoundation.lams.tool.forum.persistence.Forum;
import org.lamsfoundation.lams.tool.forum.persistence.ForumDao;
import org.lamsfoundation.lams.tool.forum.persistence.ForumUser;
import org.lamsfoundation.lams.tool.forum.test.BaseTest;

public class ForumDAOTest extends BaseTest{

	public ForumDAOTest(String name) {
		super(name);
	}
	
	public void testSave(){
		ForumUser user = new ForumUser();
		user.setFirstName("Steve");
		user.setUserId(new Long(1));
		
		Forum forum = new Forum();
		
		forum.setContentId(new Long(1));
		forum.setCreatedBy(user);
		
		ForumDao forumDao = new ForumDao();
		forumDao.saveOrUpdate(forum);
		
		Forum tForum = forumDao.getById(forum.getUid());
		assertEquals(tForum.getContentId(),new Long(1));
		assertEquals(tForum.getCreatedBy(),user);
	}
	
	public void testDelete(){
		
	}
	public void testGetByContentId(){
	}

}
