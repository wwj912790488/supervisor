package com.arcsoft.supervisor.commons.page;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for pagination to calc the page style.
 *
 * @author zw
 */
public class PageHelper {

    /**
     * The value of currently page number.
     */
    private int curPage;
    /**
     * The value of total page.
     */
    private int pageTotalCount;
    /**
     * The value of how many page can be navigated.
     */
    private int navigatePageCount;

    public List<Integer> getLeft() {
        List<Integer> left = new ArrayList<>();
        int minLeft = curPage - navigatePageCount;
        if (curPage + navigatePageCount > pageTotalCount)
            minLeft -= navigatePageCount + curPage - pageTotalCount;
        if (minLeft < 2)
            minLeft = 2;
        for (int i = minLeft; i < curPage; i++)
            left.add(i);
        return left;
    }

    public List<Integer> getRight() {
        List<Integer> right = new ArrayList<>();
        int maxRight = curPage + navigatePageCount;
        if (curPage - navigatePageCount < 2)
            maxRight += navigatePageCount - curPage + 1;
        if (maxRight > pageTotalCount - 1)
            maxRight = pageTotalCount - 1;
        for (int i = curPage + 1; i <= maxRight; i++)
            right.add(i);
        return right;
    }

    public boolean isLeftDotEnabled() {
        int minLeft = curPage - navigatePageCount;
        return minLeft > 2;
    }

    public boolean isRightDotEnabled() {
        int maxRight = curPage + navigatePageCount;
        return maxRight + 1 < pageTotalCount;
    }

    public int getPageTotalCount() {
        return pageTotalCount;
    }

    public void setPageTotalCount(int pageCount) {
        this.pageTotalCount = pageCount;
    }

    public int getNavigatePageCount() {
        return navigatePageCount;
    }

    public void setNavigatePageCount(int navigatePageCount) {
        this.navigatePageCount = navigatePageCount;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public boolean isValid(int page) {
        return page > 0 && page <= pageTotalCount;
    }

}
