<template>
    <div class="control-container">
        <!-- Nav bar -->
        <div class="ctrl-header">
            <div class="back-btn" @click="$router.back()">
                <van-icon name="arrow-left" />
            </div>
            <div class="header-center">
                <span class="header-title">VEHICLE CONTROL</span>
            </div>
            <div class="header-status">
                <div class="status-dot" :class="{ connected: carState.isOnline }"></div>
            </div>
        </div>
        
        <!-- Car Visualization -->
        <div class="car-visual">
            <img class="car-image" :src="carImage" />
            <div class="car-status-label">
                <span class="status-icon">●</span>
                <span>{{ currentStateText }}</span>
            </div>
        </div>

        <!-- Control Panels -->
        <div class="panels-wrap">
            <!-- Main Action Controls -->
            <div class="panel-section">
                <div class="panel-header">
                    <span class="panel-label">MAIN CONTROLS</span>
                </div>
                <div class="action-grid">
                    <div 
                        v-for="(item, index) in controlActions" 
                        :key="index"
                        class="action-card"
                        :class="{ active: activeControls[index] }"
                        @click="executeControl(item, index)"
                    >
                        <div class="action-icon">
                            <div class="icon-ring">
                                <van-icon :name="item.icon" size="22" />
                            </div>
                        </div>
                        <div class="action-name">{{ item.label }}</div>
                        <div class="action-status">{{ activeControls[index] ? 'ON' : 'OFF' }}</div>
                    </div>
                </div>
            </div>

            <!-- Secondary Controls -->
            <div class="panel-section">
                <div class="panel-header">
                    <span class="panel-label">QUICK FUNCTIONS</span>
                </div>
                <div class="func-list">
                    <div 
                        v-for="(item, index) in quickFunctions" 
                        :key="'f-' + index"
                        class="func-item"
                        :class="{ active: activeQuicks[index] }"
                        @click="executeQuick(item, index)"
                    >
                        <div class="func-left">
                            <div class="func-icon-dot" :style="{ background: activeQuicks[index] ? item.color : '#cbd5e1' }"></div>
                            <div>
                                <div class="func-name">{{ item.label }}</div>
                                <div class="func-desc">{{ activeQuicks[index] ? item.activeDesc : item.desc }}</div>
                            </div>
                        </div>
                        <div class="func-right">
                            <span v-if="activeQuicks[index]" class="func-status-on">已开启</span>
                            <van-icon name="arrow" class="func-arrow" />
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <Tabbar active="" />
    </div>
</template>

<script setup>
import Tabbar from "@/components/Tabbar.vue"
import { doCarStart, CarState } from '@/api/carInfo'
import controlImage from '@/assets/control/control.png'
import firingImage from '@/assets/control/firing.png'
import lightImage from '@/assets/control/light.png'
import ventilateImage from '@/assets/control/ventilate.png'
import whistleImage from '@/assets/control/whistle.png'
import { computed, ref } from 'vue'
import { useVantCompat } from '@/composables/useVantCompat'

const controlImages = {
    firing: firingImage,
    light: lightImage,
    ventilate: ventilateImage,
    whistle: whistleImage
}

defineOptions({ name: 'control' })
const { toast, notify, dialog } = useVantCompat()
const carId = ref(0)
const carState = ref({})
const activeControls = ref({})
const controlActions = ref([
                { label: '引擎启动', icon: 'fire-o', action: 'firing' },
                { label: '灯光控制', icon: 'bulb-o', action: 'light' },
                { label: '寻车鸣笛', icon: 'volume-o', action: 'whistle' },
                { label: '通风换气', icon: 'cluster-o', action: 'ventilate' },
            ])
const activeQuicks = ref({})
const quickFunctions = ref([
                { label: '空调预冷', desc: '远程开启空调', activeDesc: '空调已开启运行中', color: '#3b82f6' },
                { label: '座椅加热', desc: '远程加热座椅', activeDesc: '座椅加热中', color: '#f59e0b' },
                { label: '后备箱开启', desc: '远程开启后备箱', activeDesc: '后备箱已开启', color: '#22c55e' },
                { label: '车窗控制', desc: '一键开关车窗', activeDesc: '车窗已开启', color: '#8b5cf6' },
            ])
const carImage = computed(() => {
            // Show the first active control's image if any
            for (let i = 0; i < controlActions.value.length; i++) {
                if (activeControls.value[i]) {
                    const action = controlActions.value[i].action;
                    return controlImages[action] || controlImage;
                }
            }
            return controlImage;
        })
const currentStateText = computed(() => {
            const activeLabels = [];
            for (let i = 0; i < controlActions.value.length; i++) {
                if (activeControls.value[i]) {
                    activeLabels.push(controlActions.value[i].label);
                }
            }
            if (activeLabels.length > 0) {
                return activeLabels.join(' · ') + ' 运行中';
            }
            return '待命状态';
        })
function fetchCarState() {
            if (!carId.value) return;
            CarState(carId.value).then(res => {
                if (res.code == 200) {
                    carState.value = res.data || {};
                }
            }).catch(() => {});
        }
function executeControl(item, index) {
            if (activeControls.value[index]) {
                // Turn off
                activeControls.value[index] = false;
                toast('已关闭 ' + item.label);
                return;
            }
            // Turn on
            toast.loading({ message: item.label + '中...', forbidClick: true, duration: 1500 });
            
            doCarStart({ carId: carId.value, action: item.action }).then(res => {
                if (res.code == 200) {
                    activeControls.value[index] = true;
                    toast.success(item.label + '成功');
                } else {
                    toast.fail(res.msg || '操作失败');
                }
            }).catch(() => {
                toast.fail('网络异常');
            });
        }
function executeQuick(item, index) {
            if (activeQuicks.value[index]) {
                // Turn off
                activeQuicks.value[index] = false;
                toast('已关闭 ' + item.label);
            } else {
                // Turn on
                activeQuicks.value[index] = true;
                toast.success(item.label + '指令已发送');
            }
        }
const carInfo = window.localStorage.getItem('carInfo');
        if (carInfo) {
            const parsed = JSON.parse(carInfo);
            carId.value = parsed.carId || (parsed.car && parsed.car.carId) || 0;
        }
        fetchCarState();
</script>

<style lang="scss" scoped>
.control-container {
    min-height: 100vh;
    background: var(--bg-page);
    position: relative;
    padding-bottom: 100px;
    animation: fadeIn 0.6s ease-out;
}

/* ===== Header ===== */
.ctrl-header {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    padding-top: calc(env(safe-area-inset-top, 20px) + 12px);
    background: #fff;
    border-bottom: 1px solid var(--border);

    .back-btn {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--text-primary);
        font-size: 20px;
        cursor: pointer;
    }

    .header-center {
        .header-title {
            font-size: 14px;
            font-weight: 600;
            letter-spacing: 1px;
            color: var(--text-primary);
        }
    }

    .header-status {
        width: 32px;
        display: flex;
        justify-content: flex-end;
    }

    .status-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: var(--text-tertiary);

        &.connected {
            background: #10b981;
        }
    }
}

/* ===== Car Visual ===== */
.car-visual {
    padding: 40px 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1);

    .car-image {
        width: 260px;
        max-width: 75%;
        filter: drop-shadow(0 15px 30px rgba(0, 0, 0, 0.08));
        transition: transform 0.4s ease;
    }

    .car-status-label {
        margin-top: 24px;
        padding: 6px 16px;
        background: #fff;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        font-size: 12px;
        color: var(--text-secondary);
        display: flex;
        align-items: center;
        gap: 8px;

        .status-icon {
            color: #10b981;
            font-size: 8px;
        }
    }
}

/* ===== Panels ===== */
.panels-wrap {
    padding: 0 16px;
}

.panel-section {
    margin-bottom: 24px;
    animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
    
    &:nth-child(2) { animation-delay: 0.1s; }
}

.panel-header {
    margin-bottom: 12px;
    padding-left: 4px;

    .panel-label {
        font-size: 12px;
        font-weight: 600;
        color: var(--text-tertiary);
        letter-spacing: 1px;
    }
}

/* ===== Action Grid ===== */
.action-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
}

.action-card {
    padding: 24px 16px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.06);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    transition: all 0.3s ease;
    border: 1px solid transparent;

    .icon-ring {
        width: 52px;
        height: 52px;
        border-radius: 50%;
        background: #f8fafc;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--text-secondary);
        transition: all 0.3s ease;
    }

    .action-name {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary);
    }

    .action-status {
        font-size: 11px;
        color: var(--text-tertiary);
        font-weight: 400;
    }

    &.active {
        background: var(--accent-light);
        border-color: var(--accent);
        
        .icon-ring {
            background: #fff;
            color: var(--accent);
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
        }

        .action-name { color: var(--accent); }
        .action-status { color: var(--accent); font-weight: 600; }
    }

    &:active {
        transform: scale(0.97);
    }
}

/* ===== Function List ===== */
.func-list {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.06);
    overflow: hidden;
}

.func-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    transition: background 0.2s;

    &:not(:last-child) {
        border-bottom: 1px solid var(--border);
    }

    &:active {
        background: #f8fafc;
    }

    &.active {
        background: rgba(59, 130, 246, 0.02);

        .func-name {
            color: var(--accent);
        }
        .func-desc {
            color: var(--accent);
            opacity: 0.7;
        }
    }

    .func-left {
        display: flex;
        align-items: center;
        gap: 16px;
    }

    .func-icon-dot {
        width: 10px;
        height: 10px;
        border-radius: 50%;
        flex-shrink: 0;
        transition: background 0.3s;
    }

    .func-name {
        font-size: 15px;
        font-weight: 500;
        color: var(--text-primary);
        transition: color 0.3s;
    }

    .func-desc {
        font-size: 12px;
        color: var(--text-tertiary);
        margin-top: 2px;
        transition: color 0.3s;
    }

    .func-right {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .func-status-on {
        font-size: 11px;
        color: var(--accent);
        font-weight: 500;
    }

    .func-arrow {
        color: var(--text-tertiary);
        font-size: 16px;
    }
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
