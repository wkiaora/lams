/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */

package org.lamsfoundation.lams.rating.dao;

import org.lamsfoundation.lams.rating.dto.RatingCriteriaDTO;
import org.lamsfoundation.lams.rating.model.Rating;

public interface IRatingDAO {

    void saveOrUpdate(Object object);

    Rating getRating(Long ratingCriteriaId, Integer userId, Long itemId);

    /**
     * Returns rating statistics by particular item
     * 
     * @param itemId
     * @return
     */
    RatingCriteriaDTO getRatingAverageDTOByItem(Long ratingCriteriaId, Long itemId);

    RatingCriteriaDTO getRatingAverageDTOByUser(Long ratingCriteriaId, Long itemId, Integer userId);

    Rating get(Long uid);

    /**
     * Returns number of images rated by specified user in a current activity. It counts comments as ratings. This method
     * is applicable only for RatingCriterias of LEARNER_ITEM_CRITERIA_TYPE type.
     * 
     * @param toolContentId
     * @param userId
     * @return
     */
    int getCountItemsRatedByUser(final Long toolContentId, final Integer userId);

}
