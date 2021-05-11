<template>
  <div style="padding: 25px">
    <el-form :inline="true" :model="serchForm" class="demo-form-inline" style="height: 50px">
      <el-form-item label="">
        <el-input v-model="serchForm.word" placeholder="课程名称" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getCourseList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-dialog
      :visible.sync="dialogVisible2"
      :close-on-click-modal="false"
      :title="'课程成绩'"
      width="65%"
      z-index="3"
      @open="open2"
      center>
      <div class="historyLabel">
        <div id="historyData" class="historyCanvas"/>
      </div>
    </el-dialog>
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
      <el-table-column
        prop=""
        label="操作"
        width="200">
        <template slot-scope="courseScope">
          <el-button type="primary" @click="showScore(courseScope.row)">成绩分析</el-button>
          <!--          <el-button icon="el-icon-download" type="primary" @click="exportTemplate(courseScope.row.id, 2021)">下载导入模板</el-button>-->
        </template>
      </el-table-column>
      <el-table-column prop="courseTasks" type="expand" label="考核环节" width="800">
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
              width="400"
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
                <el-button type="primary" @click="handleCheckLinkShowScore(courseScope.row, checkLinkScope.row)">散点图
                </el-button>

                <el-button :disabled="courseScope.row.editStatus === 0"
                           v-if="checkLinkScope.row.year === new Date().getFullYear()"
                           type="warning" @click="handleCheckLinkEditForm(courseScope.row, checkLinkScope.row)">编辑成绩
                </el-button>

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
<!--          <span style="margin-left: 1%;display: inline-block">-->
<!--            <el-form :inline="true" :model="stuScoreSerchForm" class="demo-form-inline">-->
<!--              <el-form-item label="">-->
<!--                <el-input v-model="stuScoreSerchForm.stuNo" placeholder="学号" clearable/>-->
<!--              </el-form-item>-->
<!--              <el-form-item>-->
<!--                <el-button type="primary" @click="getStuScore(stuScoreSerchForm.stuNo)">查询</el-button>-->
<!--              </el-form-item>-->
<!--            </el-form>-->
<!--          </span>-->
          <span style="margin-left: 40%">
            <el-button icon="el-icon-download" type="primary" @click="exportTemplate">下载导入模板</el-button>
            <el-button icon="el-icon-upload2" type="primary">Excel导入成绩</el-button>
          </span>
          <el-form-item prop="pass" style="padding: 0;margin: 0">
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
            <div v-for="(item,index) in ckeckLinkEditForm.stuScores" type="text" autocomplete="off"
                 style="height: 35px">
              <el-input type="text" autocomplete="off" v-model="item.stuno"
                        style="width: 25%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-model="item.score"
                        style="width: 15%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.mixScore" disabled
                        style="width: 15%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.createdDate" disabled
                        style="width: 20%;margin-top: 2px"></el-input>
              <el-input type="text" autocomplete="off" v-if="item.createdDate" v-model="item.updateDate" disabled
                        style="width: 20%;margin-top: 2px"></el-input>
              <el-button type="danger" icon="el-icon-delete" circle @click="handleDeleteChecklink(index)"></el-button>
            </div>
            <div>
              <el-button type="success" icon="el-icon-plus" plain @click="addAScore" style="margin-top: 20px">添加成绩
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer" style="margin-left: 80%">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitScores">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="'课程目标:' + viewingCourseTask.describes"
      :visible.sync="dialogVisible1"
      :close-on-click-modal="false"
      width="75%"
      @open="open"
      center
    >
      <div class="elinkChart">
        <div id="stuTaskScoreBar" class="stuElinkScoreBar"></div>
        <div id="sysElinkMixPie" class="sysElinkMixPie"></div>
      </div>
      <div id="sysElinkScoreBar" class="sysElinkScoreBar"></div>
    </el-dialog>

    <el-dialog
      :title="'考核环节:' + viewingCheckLink.name"
      :visible.sync="dialogVisible3"
      :close-on-click-modal="false"
      width="75%"
      @open="open3"
      center
    >
      <div class="elinkScatterBar">
        <div id="elinkScatterPlot" class="elinkScatterPlot"></div>
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
import axios from "axios";
import {getToken} from "@/utils/auth";
import echarts from "echarts";

export default {
  name: "CheckLinkScore",
  data() {
    return {
      dialogVisible: false,
      dialogVisible1: false,
      dialogVisible2: false,
      dialogVisible3: false,
      pageSize: 20,
      total: 0,
      currentPage: 1,
      viewCourseTasks: [],
      viewingCourseTask: {},
      viewingCourse: {},
      viewingCheckLink: {},
      viewingCheckLinks: [],
      viewingStuScore: [],
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
      },
      stuScoreSerchForm: {
        stuNo: ''
      }
    }
  },
  mounted() {
    this.getCourseList()
  },
  methods: {
    getStuScore(stuNo){

    },
    showScore(course) {
      this.viewingCourse = course
      requestByClient(supplierConsumer, 'POST', 'courseTask/voList', {
        courseId: course.id
      }, res => {
        if (res.data.succ) {
          this.viewCourseTasks = res.data.data
          if (this.viewCourseTasks.length > 0) {
            this.dialogVisible2 = true
          } else {
            this.$message({
              message: '暂无成绩',
              type: 'warning'
            });
          }
        }
        this.loading = false
      })
    },
    handleCheckLinkShowScore(course, checkLink) {
      this.viewingCheckLink = checkLink
      requestByClient(supplierConsumer, 'POST', 'stuScore/list', {
        checkLinkId: this.viewingCheckLink.id
      }, res => {
        if (res.data.succ) {
          this.viewingStuScore = res.data.data
          if (this.viewingStuScore.length > 0) {
            this.dialogVisible3 = true
          } else {
            this.$message({
              message: '暂无成绩',
              type: 'warning'
            });
          }
        }
      });
    },
    open() {
      this.$nextTick(() => {
        this.setStuTaskScoreBar()
        this.setsysElinkMixPie()
      })
    },
    open2() {
      this.$nextTick(() => {
        this.setChartData(this.viewCourseTasks)
      })
    },
    open3() {
      this.$nextTick(() => {
        this.setCheckLinkScoreBar()
      })
    },
    setCheckLinkScoreBar() {
      var chartDom = document.getElementById('elinkScatterPlot');
      var myChart = echarts.init(chartDom);
      var option;
      let stuScores = this.viewingStuScore
      let data = []
      for (let i = 0; i < stuScores.length; i++) {
        let step = []
        step.push(i)
        step.push(stuScores[i].score)
        data.push(step)
      }
      console.log(data)
      option = {
        grid: {
          top: 40,
          left: 50,
          right: 40,
          bottom: 50
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        toolbox: {
          show: true,
          feature: {
            saveAsImage: {show: true}
          }
        },
        xAxis: {},
        yAxis: {},
        series: [{
          symbolSize: 15,
          data: data,
          type: 'scatter',
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }]
      };
      option && myChart.setOption(option);
    },
    setStuTaskScoreBar() {
      requestByClient(supplierConsumer, 'POST', 'stuEvaluation/statisticsByCourseTaskId', {
        id: this.viewingCourseTask.id
      }, res => {
        let data = res.data.data
        let options = ['很差', '差', '一般', '好', '很好']
        let nams = data.map(i => {
          return options[i.score - 1]
        })
        let counts = data.map(i => {
          return i.count
        })
        let chartDom1 = document.getElementById('stuTaskScoreBar');
        let myChart1 = echarts.init(chartDom1);
        let option1;
        option1 = {
          title: {
            text: '学生评价成绩分布',
            subtext: ''
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          xAxis: {
            type: 'value',
            show: false
          },
          yAxis: {
            type: 'category',
            data: nams
          },
          series: [
            {
              name: '评价次数:',
              type: 'bar',
              data: counts,
              itemStyle: {
                normal: {
                  color: '#017180'
                }
              },
            }
          ]
        };
        option1 && myChart1.setOption(option1);
      })
    },
    setsysElinkMixPie() {
      requestByClient(supplierConsumer, 'POST', 'courseTaskCheckLink/voList', {
        courseTaskId: this.viewingCourseTask.id
      }, res => {
        let data = res.data.data
        let nams = data.map(i => {
          return i.checkLink.name
        })
        let counts = data.map(i => {
          return ((i.checkLink.averageScore / i.checkLink.targetScore) * 100).toFixed(2)
        })
        let chartDom1 = document.getElementById('sysElinkScoreBar');
        let myChart1 = echarts.init(chartDom1);
        let option1;
        option1 = {
          title: {
            text: '考核环节成绩分布',
            subtext: ''
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          yAxis: {
            type: 'value',
            boundaryGap: [0, 0.01],
            max: 100
          },
          xAxis: {
            type: 'category',
            data: nams,
          },
          series: [
            {
              name: '',
              type: 'bar',
              data: counts,
              itemStyle: {
                normal: {
                  color: '#32006a'
                }
              },
            }
          ]
        };
        option1 && myChart1.setOption(option1);


        let chartDom2 = document.getElementById('sysElinkMixPie');
        let myChart2 = echarts.init(chartDom2);
        let option2;
        let pieData = data.map(i => {
          return {
            value: i.mix,
            name: i.checkLink.name
          }
        })

        option2 = {
          title: {
            text: '此课程目标各考核环节权重',
            subtext: '',
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
          },
          series: [
            {
              name: '',
              type: 'pie',
              radius: '50%',
              data: pieData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        };

        option2 && myChart2.setOption(option2);
      })
    },
    setChartData(data) {
      let courseTasksName = data.map(i => {
        return (i.course.name + ':' + i.describes)
      })
      let sysScores = data.map(i => {
        return i.sysGrade ? (i.sysGrade).toFixed(2) * 100 : 0
      })
      let stuScores = data.map(i => {
        return i.stuGrade ? (i.stuGrade).toFixed(2) * 100 : 0
      })
      let vue = this
      const chartDom = document.getElementById('historyData')
      const myChart = echarts.init(chartDom)
      let option
      option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        dataZoom: [
          {
            id: 'dataZoomX',
            type: 'slider',
            xAxisIndex: [0],
            filterMode: 'filter'
          }
        ],
        toolbox: {
          show: true,
          feature: {
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },
        calculable: true,
        title: {
          text: '课程目标成绩对比分析',
          subtext: ''
        },
        xAxis: {
          type: 'category',
          data: courseTasksName,
          axisLabel: {
            interval: 0,
            rotate: 90
          }
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: [{
          name: '系统成绩',
          data: sysScores,
          barGap: 0,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#ff1272'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }, {
          name: '学生评价',
          data: stuScores,
          type: 'bar',
          itemStyle: {
            normal: {
              color: '#0e4fff'
            }
          },
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180,180,180,0.2)'
          },
          markPoint: {
            data: [
              {type: 'max', name: '最大值'}
            ]
          },
          markLine: {
            data: [
              {type: 'average', name: '平均值'}
            ]
          }
        }
        ]
      }
      option && myChart.setOption(option)

      //点击事件
      myChart.on('click', function (params) {
        vue.selectOneCourseTask(data[params.dataIndex].id)
      });
    },
    selectOneCourseTask(id) {
      requestByClient(supplierConsumer, 'POST', 'courseTask/getOne', {
        id: id
      }, res => {
        this.viewingCourseTask = res.data.data
        this.dialogVisible1 = true
      })
    },
    exportTemplate(courseId, year) {
      let baseURL = supplierConsumer.defaults.baseURL
      axios.post(baseURL + '/stuScore/exportTemplate', {
        courseId: courseId,
        year: year
      }, {
        responseType: 'blob',
        headers: {
          '_token': getToken()
        }
      }).then(res => {
        const blob = new Blob([res.data], {
          type: 'application/vnd.ms-excel'
        })
        const objectUrl = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = objectUrl
        a.download = '导入模板.xlsx'
        // a.click();
        // 下面这个写法兼容火狐
        a.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}))
        window.URL.revokeObjectURL(objectUrl)
      })
    },
    submitScores() {
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
    addAScore() {
      this.ckeckLinkEditForm.stuScores.push({stuno: '', score: '', checkLinkId: this.ckeckLinkEditForm.checkLink.id})
    },
    handleDeleteChecklink(index) {
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
    inputScore() {
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
    checkAvgScore(avg, target, index) {
      if (avg > target || avg < 0) {
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
      requestByClient(supplierConsumer, 'POST', 'stuScore/list', {checkLinkId: checkLink.id}, res => {
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
    deleteDiscribe(index) {
      var check = this.ckeckLinkEditForm.checkLinks[index]
      if (check.id) {
        requestByClient(supplierConsumer, 'POST', 'checkLink/delete', {
          id: check.id
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
      }
      this.ckeckLinkEditForm.checkLinks.splice(index, 1)
    },
    handleAddCheckLink() {
      this.ckeckLinkEditForm.checkLinks.push({
        name: '',
        mix: '',
        targetScore: '',
        taskId: this.ckeckLinkEditForm.courseTask.id
      })
    },
    submitCheckLinksForm() {
      let checkLinks = this.ckeckLinkEditForm.checkLinks
      for (let checkLink of checkLinks) {
        if (checkLink.averageScore === '') {
          this.$message({
            message: '平均成绩不得为空',
            type: 'error'
          });
          return false
        }
      }
      this.loading = true
      requestByClient(supplierConsumer, 'POST', 'checkLink/saveOrUpdate', this.ckeckLinkEditForm.checkLinks, res => {
        if (res.data.succ) {
          this.dialogVisible = false;
          this.$message({
            message: '修改成功',
            type: 'success'
          });
        } else {
          this.$message.error(res.data.msg);
        }
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.historyLabel {
  width: 97%;
  height: 550px;
}

.historyCanvas {
  width: 1150px;
  height: 530px;
}

.elinkChart {
  display: inline-block;
  width: 60%;
}

.stuElinkScoreBar {
  width: 100%;
  height: 240px;
}

.sysElinkMixPie {
  width: 100%;
  height: 370px;
}

.sysElinkScoreBar {
  display: inline-block;
  width: 40%;
  height: 600px;
}

.elinkScatterBar {
  width: 90%;
  height: 400px;
}

.elinkScatterPlot {
  width: 1350px;
  height: 400px;
}
</style>
