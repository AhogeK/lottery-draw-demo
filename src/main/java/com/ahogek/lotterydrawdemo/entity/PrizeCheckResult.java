package com.ahogek.lotterydrawdemo.entity;

import java.time.LocalDate;

/**
 * @author AhogeK
 * @since 2024-11-27 13:00:32
 */
public class PrizeCheckResult {

    private Integer firstPrize;

    private LocalDate firstPrizeLastDate;

    private Integer secondPrize;

    private LocalDate secondPrizeLastDate;

    private Integer thirdPrize;

    private LocalDate thirdPrizeLastDate;

    private Integer fourthPrize;

    private LocalDate fourthPrizeLastDate;

    private Integer fifthPrize;

    private LocalDate fifthPrizeLastDate;

    private Integer sixthPrize;

    private LocalDate sixthPrizeLastDate;

    private Integer seventhPrize;

    private LocalDate seventhPrizeLastDate;

    private Integer eighthPrize;

    private LocalDate eighthPrizeLastDate;

    private Integer ninthPrize;

    private LocalDate ninthPrizeLastDate;

    public PrizeCheckResult() {
    }

    public PrizeCheckResult(Integer firstPrize, Integer secondPrize, Integer thirdPrize, Integer fourthPrize,
                            Integer fifthPrize, Integer sixthPrize, Integer seventhPrize, Integer eighthPrize,
                            Integer ninthPrize) {
        this.firstPrize = firstPrize;
        this.secondPrize = secondPrize;
        this.thirdPrize = thirdPrize;
        this.fourthPrize = fourthPrize;
        this.fifthPrize = fifthPrize;
        this.sixthPrize = sixthPrize;
        this.seventhPrize = seventhPrize;
        this.eighthPrize = eighthPrize;
        this.ninthPrize = ninthPrize;
    }

    public PrizeCheckResult(Integer firstPrize, LocalDate firstPrizeLastDate, Integer secondPrize,
                            LocalDate secondPrizeLastDate, Integer thirdPrize, LocalDate thirdPrizeLastDate,
                            Integer fourthPrize, LocalDate fourthPrizeLastDate, Integer fifthPrize,
                            LocalDate fifthPrizeLastDate, Integer sixthPrize, LocalDate sixthPrizeLastDate,
                            Integer seventhPrize, LocalDate seventhPrizeLastDate, Integer eighthPrize,
                            LocalDate eighthPrizeLastDate, Integer ninthPrize, LocalDate ninthPrizeLastDate) {
        this.firstPrize = firstPrize;
        this.firstPrizeLastDate = firstPrizeLastDate;
        this.secondPrize = secondPrize;
        this.secondPrizeLastDate = secondPrizeLastDate;
        this.thirdPrize = thirdPrize;
        this.thirdPrizeLastDate = thirdPrizeLastDate;
        this.fourthPrize = fourthPrize;
        this.fourthPrizeLastDate = fourthPrizeLastDate;
        this.fifthPrize = fifthPrize;
        this.fifthPrizeLastDate = fifthPrizeLastDate;
        this.sixthPrize = sixthPrize;
        this.sixthPrizeLastDate = sixthPrizeLastDate;
        this.seventhPrize = seventhPrize;
        this.seventhPrizeLastDate = seventhPrizeLastDate;
        this.eighthPrize = eighthPrize;
        this.eighthPrizeLastDate = eighthPrizeLastDate;
        this.ninthPrize = ninthPrize;
        this.ninthPrizeLastDate = ninthPrizeLastDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getFirstPrize() {
        return firstPrize;
    }

    public void setFirstPrize(Integer firstPrize) {
        this.firstPrize = firstPrize;
    }

    public Integer getSecondPrize() {
        return secondPrize;
    }

    public void setSecondPrize(Integer secondPrize) {
        this.secondPrize = secondPrize;
    }

    public Integer getThirdPrize() {
        return thirdPrize;
    }

    public void setThirdPrize(Integer thirdPrize) {
        this.thirdPrize = thirdPrize;
    }

    public Integer getFourthPrize() {
        return fourthPrize;
    }

    public void setFourthPrize(Integer fourthPrize) {
        this.fourthPrize = fourthPrize;
    }

    public Integer getFifthPrize() {
        return fifthPrize;
    }

    public void setFifthPrize(Integer fifthPrize) {
        this.fifthPrize = fifthPrize;
    }

    public Integer getSixthPrize() {
        return sixthPrize;
    }

    public void setSixthPrize(Integer sixthPrize) {
        this.sixthPrize = sixthPrize;
    }

    public Integer getSeventhPrize() {
        return seventhPrize;
    }

    public void setSeventhPrize(Integer seventhPrize) {
        this.seventhPrize = seventhPrize;
    }

    public Integer getEighthPrize() {
        return eighthPrize;
    }

    public void setEighthPrize(Integer eighthPrize) {
        this.eighthPrize = eighthPrize;
    }

    public Integer getNinthPrize() {
        return ninthPrize;
    }

    public void setNinthPrize(Integer ninthPrize) {
        this.ninthPrize = ninthPrize;
    }

    @Override
    public String toString() {
        return String.format("""
                        历史中奖统计:
                                    一等奖: %d次 (奖金: 浮动)%s
                                    二等奖: %d次 (奖金: 浮动)%s
                                    三等奖: %d次 (奖金: 10,000元)%s
                                    四等奖: %d次 (奖金: 3,000元)%s
                                    五等奖: %d次 (奖金: 300元)%s
                                    六等奖: %d次 (奖金: 200元)%s
                                    七等奖: %d次 (奖金: 100元)%s
                                    八等奖: %d次 (奖金: 15元)%s
                                    九等奖: %d次 (奖金: 5元)%s
                        """,
                firstPrize, firstPrize > 0 ? String.format(" 最新中奖: %s", firstPrizeLastDate) : "",
                secondPrize, secondPrize > 0 ? String.format(" 最新中奖: %s", secondPrizeLastDate) : "",
                thirdPrize, thirdPrize > 0 ? String.format(" 最新中奖: %s", thirdPrizeLastDate) : "",
                fourthPrize, fourthPrize > 0 ? String.format(" 最新中奖: %s", fourthPrizeLastDate) : "",
                fifthPrize, fifthPrize > 0 ? String.format(" 最新中奖: %s", fifthPrizeLastDate) : "",
                sixthPrize, sixthPrize > 0 ? String.format(" 最新中奖: %s", sixthPrizeLastDate) : "",
                seventhPrize, seventhPrize > 0 ? String.format(" 最新中奖: %s", seventhPrizeLastDate) : "",
                eighthPrize, eighthPrize > 0 ? String.format(" 最新中奖: %s", eighthPrizeLastDate) : "",
                ninthPrize, ninthPrize > 0 ? String.format(" 最新中奖: %s", ninthPrizeLastDate) : ""
        );
    }

    public static class Builder {
        private Integer firstPrize;

        private LocalDate firstPrizeLastDate;

        private Integer secondPrize;

        private LocalDate secondPrizeLastDate;

        private Integer thirdPrize;

        private LocalDate thirdPrizeLastDate;

        private Integer fourthPrize;

        private LocalDate fourthPrizeLastDate;

        private Integer fifthPrize;

        private LocalDate fifthPrizeLastDate;

        private Integer sixthPrize;

        private LocalDate sixthPrizeLastDate;

        private Integer seventhPrize;

        private LocalDate seventhPrizeLastDate;

        private Integer eighthPrize;

        private LocalDate eighthPrizeLastDate;

        private Integer ninthPrize;

        private LocalDate ninthPrizeLastDate;

        // 保持原有的方法兼容性
        public Builder firstPrize(Integer firstPrize) {
            return firstPrize(firstPrize, null);
        }

        public Builder firstPrize(Integer firstPrize, LocalDate lastDate) {
            this.firstPrize = firstPrize == null ? 0 : firstPrize;
            this.firstPrizeLastDate = lastDate;
            return this;
        }

        public Builder secondPrize(Integer secondPrize) {
            return secondPrize(secondPrize, null);
        }

        public Builder secondPrize(Integer secondPrize, LocalDate lastDate) {
            this.secondPrize = secondPrize == null ? 0 : secondPrize;
            this.secondPrizeLastDate = lastDate;
            return this;
        }

        public Builder thirdPrize(Integer thirdPrize) {
            return thirdPrize(thirdPrize, null);
        }

        public Builder thirdPrize(Integer thirdPrize, LocalDate lastDate) {
            this.thirdPrize = thirdPrize == null ? 0 : thirdPrize;
            this.thirdPrizeLastDate = lastDate;
            return this;
        }

        public Builder fourthPrize(Integer fourthPrize) {
            return fourthPrize(fourthPrize, null);
        }

        public Builder fourthPrize(Integer fourthPrize, LocalDate lastDate) {
            this.fourthPrize = fourthPrize == null ? 0 : fourthPrize;
            this.fourthPrizeLastDate = lastDate;
            return this;
        }

        public Builder fifthPrize(Integer fifthPrize) {
            return fifthPrize(fifthPrize, null);
        }

        public Builder fifthPrize(Integer fifthPrize, LocalDate lastDate) {
            this.fifthPrize = fifthPrize == null ? 0 : fifthPrize;
            this.fifthPrizeLastDate = lastDate;
            return this;
        }

        public Builder sixthPrize(Integer sixthPrize) {
            return sixthPrize(sixthPrize, null);
        }

        public Builder sixthPrize(Integer sixthPrize, LocalDate lastDate) {
            this.sixthPrize = sixthPrize == null ? 0 : sixthPrize;
            this.sixthPrizeLastDate = lastDate;
            return this;
        }

        public Builder seventhPrize(Integer seventhPrize) {
            return seventhPrize(seventhPrize, null);
        }

        public Builder seventhPrize(Integer seventhPrize, LocalDate lastDate) {
            this.seventhPrize = seventhPrize == null ? 0 : seventhPrize;
            this.seventhPrizeLastDate = lastDate;
            return this;
        }

        public Builder eighthPrize(Integer eighthPrize) {
            return eighthPrize(eighthPrize, null);
        }

        public Builder eighthPrize(Integer eighthPrize, LocalDate lastDate) {
            this.eighthPrize = eighthPrize == null ? 0 : eighthPrize;
            this.eighthPrizeLastDate = lastDate;
            return this;
        }

        public Builder ninthPrize(Integer ninthPrize) {
            return ninthPrize(ninthPrize, null);
        }

        public Builder ninthPrize(Integer ninthPrize, LocalDate lastDate) {
            this.ninthPrize = ninthPrize == null ? 0 : ninthPrize;
            this.ninthPrizeLastDate = lastDate;
            return this;
        }

        public PrizeCheckResult build() {
            return new PrizeCheckResult(
                    firstPrize, firstPrizeLastDate,
                    secondPrize, secondPrizeLastDate,
                    thirdPrize, thirdPrizeLastDate,
                    fourthPrize, fourthPrizeLastDate,
                    fifthPrize, fifthPrizeLastDate,
                    sixthPrize, sixthPrizeLastDate,
                    seventhPrize, seventhPrizeLastDate,
                    eighthPrize, eighthPrizeLastDate,
                    ninthPrize, ninthPrizeLastDate
            );
        }
    }
}
