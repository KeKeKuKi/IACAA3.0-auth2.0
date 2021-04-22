<template>
  <div>
    <el-form :inline="true" :model="serchForm" class="demo-form-inline" style="height: 50px">
      <el-form-item label="">
        <el-input v-model="serchForm.word" placeholder="名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList()">查询</el-button>
      </el-form-item>
      <span style="float: right;margin-right: 30px">
        <el-form-item>
          <el-button type="warning" @click="handleAddCourse()">新增</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleDelete()">删除</el-button>
        </el-form-item>
      </span>
    </el-form>
    <el-table
      ref="multipleTable"
      :data="tableData"
      style="width: 100%"
      height="750"
      tooltip-effect="dark"
      @selection-change="handleSelectionChange">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        prop="id"
        label="课程编号"
        width="100">
      </el-table-column>
      <el-table-column
        prop="name"
        label="名称"
        width="200">
      </el-table-column>
      <el-table-column
        prop="image"
        label="简介"
        width="600">
      </el-table-column>
      <el-table-column
        prop="editUserId"
        label="课程管理员"
        width="350">
        <template slot-scope="scope">
          <el-select v-model="scope.row.editUserId" @change="changeCourseEditer(scope.row)" placeholder="选择管理员" clearable filterable style="width: 60%;margin-top: 10px">
            <el-option v-for="(item1,index1) in teachers" :key="index1" :label="item1.username" :value="item1.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="课程可编辑">
        <template slot-scope="scope">
          <el-switch
            v-model="editAbles[scope.$index]"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="changeEditStatus(scope.row.id)"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" >
<!--        <template slot-scope="scope">-->
<!--          <el-button type="primary" @click="handleEditForm(scope.row)">编辑课程目标</el-button>-->
<!--        </template>-->
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[10, 15, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </div>
</template>

<script>
import {requestByClient, supplierConsumer, User} from "@/utils/HttpUtils";

export default {
  name: "Course",
  mounted() {
    this.getList()
    this.getTeachers()
  },
  data() {
    return {
      pageSize: 20,
      total : 0,
      currentPage: 1,
      editAbles:[],
      tableData: [],
      serchForm: {
        word: ''
      },
      ids:[],
      teachers: []
    }
  },methods: {
    changeCourseEditer(course){
      requestByClient(supplierConsumer, 'POST', 'course/update', course, res => {
        if (res.data.succ){
          this.$message({
            message: '已保存修改',
            type: 'success'
          });
        }
      })
    },
    getTeachers(){
      requestByClient(User, 'POST', 'api/user/list', {
      }, res => {
        if (res.data.code === 0){
          this.teachers = res.data.data
        }
      })
    },
    changeEditStatus(id) {
      requestByClient(supplierConsumer, 'POST', 'course/changeEditStatus', {
        id: id
      }, res => {
        if (res.data.succ) {
          this.$message({
            message: '修改成功',
            type: 'success'
          });
        }
      })
    },
    getList() {
      this.loading = true
      requestByClient(supplierConsumer, 'POST', 'course/adminList', {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        word: this.serchForm.word
      }, res => {
        if (res.data.succ) {
          this.tableData = res.data.data.list
          this.editAbles = this.tableData.map(i => {
            return i.editStatus === 1
          })
          this.total = res.data.data.total
          this.pageSize = res.data.data.pageSize
          this.currentPage = res.data.data.pageNum
        }
        this.loading = false
      })
    },
    getUser() {
      requestByClient(User, 'POST', 'course/voList', {
      }, res => {
        if (res.data.succ) {
          console.log(res)
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },
    handleDelete(){
      this.loading = true
      requestByClient(supplierConsumer, 'POST','course/del',{
          ids : this.ids}
        ,res => {
          if (res.data.succ) {
            this.$message({
              message: '删除成功',
              type: 'success'
            });
            this.getList()
          }
          this.loading = false
        })
    },
    handleSelectionChange(val) {
      const result = val.map(item => item.id)
      this.ids = result;
    },
  }
}
</script>

<style scoped>

</style>
