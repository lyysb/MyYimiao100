package com.yimiao100.sale.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 亿苗通 on 2016/8/10.
 */
public class PagedResultBean {

    private int totalPage;
    private int pageSize;
    private int page;
    /**
     * id : 4
     * title : 专家鼓励宝宝打计划外疫苗
     * newsContent :
     * newsAbstract : 孩子打预防针，医学上又称为预防接种，有计划地进行预防接种，可以提高人群的免疫力，控制传染病的流行。通过医学卫生知识的广泛宣传，人们已经有了最初步的常识。打防疫针已经被人们广泛接受。
     * newsSource : 99健康网
     * layoutType : 1
     * score : 0
     * commentNumber : 0
     * createdAt : 1470811949000
     * updatedAt : 1470815875000
     * userScoreStatus : 0
     * imageList : [{"id":6,"imagePath":"2016/08/10/e9996dc07b0f4069880d792c32fdb036.jpg","imageUrl":"http://ob8083hy1.bkt.clouddn.com/2016/08/10/e9996dc07b0f4069880d792c32fdb036.jpg","createdAt":null,"updatedAt":null}]
     * tagList : []
     */

    private List<PagedListBean> pagedList;
    private ArrayList<InformationGroupList> groupList;

    public ArrayList<InformationGroupList> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<InformationGroupList> groupList) {
        this.groupList = groupList;
    }

    /**
     * @return 总页数
     */
    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * @return 每页对象个数
     */
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return 当前页
     */
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return 分页列表
     */
    public List<PagedListBean> getPagedList() {
        return pagedList;
    }

    public void setPagedList(List<PagedListBean> pagedList) {
        this.pagedList = pagedList;
    }
}
