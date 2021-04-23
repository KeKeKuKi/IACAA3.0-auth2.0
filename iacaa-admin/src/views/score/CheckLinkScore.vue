<template>
  <div style="padding: 25px">
   <el-form :inline="true" :model="serchForm" class="demo-form-inline" style="height: 50px">
      <el-form-item label="">
        <el-input v-model="serchForm.word" placeholder="课程名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getCourseList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="multipleTable"
      :data="courses"
      style="width: 100%"
      height="750"
      tooltip-effect="dark"
    >
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
        width="400">
      </el-table-column>
        <el-table-column prop="courseTasks" type="expand" label="考核环节" width="1000">
        <template slot-scope="courseScope">
          <el-table :data="courseScope.row.checkLinks" stripe>
            <el-table-column
              prop="year"
              label="年份"
              width="200"
            />
            <el-table-column
              prop="name"
              label="考核环节"
              width="500"
            />
            <el-table-column
              prop="createdDate"
              label="创建时间"
              width="200"
            />
            <el-table-column
              prop="updateDate"
              label="最终更新时间"
              width="200"
            />
            <el-table-column label="操作" prop="courseTasks">
              <template slot-scope="checkLinkScope">
                <el-button :disabled="courseScope.row.editStatus === 0" v-if="checkLinkScope.row.year === new Date().getFullYear()"
                           type="primary" @click="handleCheckLinkEditForm(courseScope.row, checkLinkScope.row)" >编辑成绩</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :title="ckeckLinkEditForm.course.name +' '+ ckeckLinkEditForm.checkLink.name+' (目标总分：'+ckeckLinkEditForm.checkLink.targetScore+')'"
      width="65%"
      center>
      <div>
        <el-form ref="ruleForm" :model="ckeckLinkEditForm" status-icon class="demo-ruleForm">
          <span style="margin-left: 70%">
            <el-button icon="el-icon-download" type="primary">下载导入模板</el-button>
            <el-button icon="el-icon-upload2" type="primary">Excel导入成绩</el-button>
          </span>
          <el-form-item  prop="pass" style="padding: 0;margin: 0">
            <br>
            <el-table
              ref="multipleTable"
              style="width: 100%"
              height="50"
              tooltip-effect="dark">
            <el-table-column
              prop=""
              label="学号"
              width="300">
            </el-table-column>
            <el-table-column
              prop=""
              label="成绩"
              width="170">
            </el-table-column>
            <el-table-column
              prop=""
              label="成绩百分比"
              width="170">
            </el-table-column>
            <el-table-column
                prop=""
                label="创建日期"
                width="250">
            </el-table-column>
            <el-table-column
                prop=""
                label="更新日期">
            </el-table-column>
          </el-table>
            <div v-for="(item,index) in ckeckLinkEditForm.stuScores" type="text" autocomplete="off">
              <el-input type="text" autocomplete="off" v-model="item.stuno" style="width: 25%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-model="item.score" style="width: 15%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.mixScore" disabled style="width: 15%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.createdDate" disabled style="width: 20%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.updateDate" disabled style="width: 20%;margin-top: 2px"></el-input>
              <el-button type="danger" icon="el-icon-delete" circle @click="handleDeleteChecklink(index)"></el-button>
            </div>
            <div>
              <el-button type="success" icon="el-icon-plus" plain @click="addAScore" style="margin-top: 20px">添加成绩</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer" style="margin-left: 80%">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitScores">确 定</el-button>
      </div>
    </el-dialog>
    <el-pagination
      :current-page="currentPage"
      :page-sizes="[10, 15, 20, 50, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>

</template>

<script>
import {requestByClient, supplierConsumer} from "@/utils/HttpUtils";

export default {
  name: "CheckLinkScore",
  data(){
    return{
      dialogVisible: false,
      pageSize: 20,
      total: 0,
      currentPage: 1,
      tableData: [],
      courses: [],
      serchForm: {
        word: '',
        year: ''
      },
      ckeckLinkEditForm: {
        course: {},
        checkLink: {},
        stuScores: []
      }
    }
  },
  mounted() {
    this.getCourseList()
  },
  methods:{
    submitScores(){
      requestByClient(supplierConsumer, 'POST', 'stuScore/saveOrUpdate', this.ckeckLinkEditForm.stuScores, res => {
        if (res.data.succ) {
          this.$message({
            message: '修改成功',
            type: 'success'
          });
          this.dialogVisible = false
        }
      })
    },
    addAScore(){
      this.ckeckLinkEditForm.stuScores.push({stuno: '',score: '',checkLinkId: this.ckeckLinkEditForm.checkLink.id})
    },
    handleDeleteChecklink(index){
      var checkLink = this.ckeckLinkEditForm.stuScores[index]
      if (checkLink.id) {
        this.$confirm('确定删除此条成绩?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          requestByClient(supplierConsumer, 'POST', 'stuScore/delete', {
            id: checkLink.id
          }, res => {
            if (res.data.succ) {
              this.$message({
                message: '已删除',
                type: 'success'
              });
            } else {
              this.$message.error(res.data.msg);
            }
            this.loading = false
          })
          this.ckeckLinkEditForm.stuScores.splice(index, 1)
        }).catch(() => {
        });
      } else {
        this.ckeckLinkEditForm.stuScores.splice(index, 1)
      }
    },
    inputScore(){
      requestByClient(supplierConsumer, 'POST', 'voList/voList', {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        word: this.serchForm.word
      }, res => {
        if (res.data.succ) {
          this.courses = res.data.data.list
          this.total = res.data.data.total
          this.pageSize = res.data.data.pageSize
          this.currentPage = res.data.data.pageNum
        }
      })
    },
    checkAvgScore(avg,target,index){
      if(avg > target || avg < 0){
        this.$message({
          message: '平均成绩不得大于目标成绩且不小于零',
          type: 'error'
        });
        this.ckeckLinkEditForm.checkLinks[index].averageScore = ''
      }
    },
    getCourseList() {
      requestByClient(supplierConsumer, 'POST', 'course/voList', {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        word: this.serchForm.word
      }, res => {
        if (res.data.succ) {
          this.courses = res.data.data.list
          this.total = res.data.data.total
          this.pageSize = res.data.data.pageSize
          this.currentPage = res.data.data.pageNum
        }
      })
    },
    handleCheckLinkEditForm(course, checkLink) {
      this.dialogVisible = true
      this.ckeckLinkEditForm.checkLink = checkLink
      this.ckeckLinkEditForm.course = course
      requestByClient(supplierConsumer, 'POST', 'stuScore/list', {checkLinkId: checkLink.id }, res => {
        if (res.data.succ) {
          this.ckeckLinkEditForm.stuScores = res.data.data
        }
        this.loading = false
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.getCourseList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.getCourseList()
    },
    deleteDiscribe(index){
      var check = this.ckeckLinkEditForm.checkLinks[index]
      if(check.id){
        requestByClient(supplierConsumer, 'POST','checkLink/delete', {
          id: check.id
        },res => {
          if (res.data.succ) {
            this.$message({
              message: '已删除',
              type: 'success'
            });
          }else {
            this.$message.error(res.data.msg);
          }
          this.loading = false
        })
      }
      this.ckeckLinkEditForm.checkLinks.splice(index,1)
    },
    handleAddCheckLink(){
      this.ckeckLinkEditForm.checkLinks.push({name:'',mix: '',targetScore: '',taskId: this.ckeckLinkEditForm.courseTask.id})
    },
    submitCheckLinksForm(){
      let checkLinks = this.ckeckLinkEditForm.checkLinks
      for (let checkLink of checkLinks) {
        if(checkLink.averageScore === ''){
          this.$message({
            message: '平均成绩不得为空',
            type: 'error'
          });
          return false
        }
      }
      this.loading = true
      requestByClient(supplierConsumer, 'POST','checkLink/saveOrUpdate', this.ckeckLinkEditForm.checkLinks,res => {
        if (res.data.succ) {
          this.dialogVisible = false;
          this.$message({
            message: '修改成功',
            type: 'success'
          });
        }else {
          this.$message.error(res.data.msg);
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>

</style>
