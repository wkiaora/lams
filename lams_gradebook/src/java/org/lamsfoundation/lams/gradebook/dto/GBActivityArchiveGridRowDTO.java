/****************************************************************
 * Copyright (C) 2008 LAMS Foundation (http://lamsfoundation.org)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.gradebook.dto;

import java.util.ArrayList;
import java.util.Date;

import org.lamsfoundation.lams.gradebook.util.GBGridView;
import org.lamsfoundation.lams.gradebook.util.GradebookUtil;

public class GBActivityArchiveGridRowDTO extends GradebookGridRowDTO {

    private Date archiveDate;
    private Double lessonMark;

    public GBActivityArchiveGridRowDTO(int attemptNumber, Date archiveDate, Double lessonMark) {
	this.id = String.valueOf(attemptNumber);
	this.archiveDate = archiveDate;
	this.lessonMark = lessonMark;
    }

    @Override
    public ArrayList<String> toStringArray(GBGridView view) {
	ArrayList<String> ret = new ArrayList<String>();
	ret.add(id);
	ret.add(archiveDate != null ? convertDateToString(archiveDate, null) : CELL_EMPTY);
	ret.add(lessonMark.toString());
	ret.add(status);
	ret.add(timeTaken != null ? convertTimeToString(timeTaken) : CELL_EMPTY);
	ret.add(startDate != null ? convertDateToString(startDate, null) : CELL_EMPTY);
	ret.add(finishDate != null ? convertDateToString(finishDate, null) : CELL_EMPTY);
	ret.add(feedback);
	ret.add(mark != null ? GradebookUtil.niceFormatting(mark) : CELL_EMPTY);

	return ret;
    }
}