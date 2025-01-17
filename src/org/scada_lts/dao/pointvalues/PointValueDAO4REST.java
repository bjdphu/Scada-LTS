/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.dao.pointvalues;

import java.sql.SQLException;
import java.util.Date;

import com.serotonin.mango.rt.dataImage.AnnotatedPointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.point.PointValueTypeOfREST;
import org.scada_lts.mango.service.PointValueService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.serotonin.mango.rt.dataImage.PointValueTime;

/** 
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
@Repository
@Deprecated
public class PointValueDAO4REST {
	
	private static final Log LOG = LogFactory.getLog(PointValueDAO4REST.class);
	
	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public PointValueTime save(String value, int typePointValueOfREST, int dpid) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("save data from REST for point:" + dpid);
		}
		
		PointValueTime pvt = null;
		
		String interpretationBinaryValueFalseOfREST = "0";
		String interpretationBinaryValueTrueOfREST = "1";
		
		if (typePointValueOfREST==PointValueTypeOfREST.TYPE_BINARY) {
			if (value.equals(interpretationBinaryValueTrueOfREST)) {
				pvt = new PointValueTime(true, new Date().getTime() );

			} else if (value.equals(interpretationBinaryValueFalseOfREST)) {
				pvt = new PointValueTime(false, new Date().getTime() );
			} else {
			   new RuntimeException("Value not compatible with type (binary)");
			}
		} else if (typePointValueOfREST==PointValueTypeOfREST.TYPE_MULTISTATE) {
			try {
				pvt = new PointValueTime(Integer.parseInt(value), new Date().getTime() );
			} catch (NumberFormatException e) {
				new RuntimeException("Value not compatible with type (multistate)");
			}
		} else if (typePointValueOfREST==PointValueTypeOfREST.TYPE_DOUBLE) {
			try {
				pvt = new PointValueTime(Double.parseDouble(value), new Date().getTime() );
			} catch (NumberFormatException e) {
				new RuntimeException("Value not compatible with type (double)");
			}
		} else if (typePointValueOfREST==PointValueTypeOfREST.TYPE_STRING) {
			pvt = new AnnotatedPointValueTime(MangoValue.objectToValue(value), new Date().getTime(), SetPointSource.Types.REST_API, 0);
		} else {
			new RuntimeException("Unknown value type");
		}

		if(pvt != null) {
			PointValueService pointValueService = new PointValueService();
			pointValueService.savePointValue(dpid, pvt);
		}
		
		return pvt;
	}

}
