package eionet.cr.dao.util;

import java.util.List;

import eionet.cr.dto.HarvestScriptDTO;

/**
 *
 * Container class for Post Harvest scripts related to one Type or source.
 * Includes helper methods for changing the scripts order etc.
 *
 */
public class HarvestScriptSet {
    /** private script container. */
    private List<HarvestScriptDTO> scripts;

    /** holder for minimal Position. */
    private int minPos;

    /** holder for maximum Position. */
    private int maxPos;


    /**
     * Initializes the container based on script lists.
     * @param phScripts Post harvest scripts list
     */
    public HarvestScriptSet(List<HarvestScriptDTO> phScripts) {
        this.scripts = phScripts;

        init();
    }


    /**
     * Returns Post harvest script by given position.
     * @param position position number (not the array position)
     * @return Matching Post harvest script. Null if no script with this position
     */
    public HarvestScriptDTO getScriptByPosition(int position) {
        for (HarvestScriptDTO script :  scripts)
            if (script.getPosition() == position) {
                return script;
            }
        return null;
    }

    /**
     * Returns position of the last script.
     *
     * @return int
     */
    public int getMaxPosition() {
        return maxPos;
    }

    /**
     * Returns position of the first script.
     *
     * @return int
     */
    public int getMinPosition() {
        return minPos;
    }


    /**
     * Inits min max values.
     */

    private void init() {
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (HarvestScriptDTO script : scripts) {
               int pos = script.getPosition();
            if (pos < minVal) {
                minVal = pos;
            }
            if (pos > maxVal) {
                maxVal = pos;
            }
        }

        this.minPos = minVal;
        this.maxPos = maxVal;
    }
}
